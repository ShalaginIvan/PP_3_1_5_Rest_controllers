const csrfHeader = document.querySelector("meta[name='csrf-header']").content;
const csrfToken = document.querySelector("meta[name='csrf-token']").content;
const headers = {
    [csrfHeader]: csrfToken,
    // добавил, чтобы сервер понимал, что в теле передается json
    'Content-Type': 'application/json',
    'Accept': 'application/json',
};
const userFetchService = {
    findAllUsers: async () => await fetch('http://localhost:8080/admin/users'),
    findOneUser: async (id) => await fetch(`http://localhost:8080/admin/user?id=${id}`),
    addNewUser: async (user) =>
        await fetch('http://localhost:8080/admin/users', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(user),
        }),
    updateUser: async (user) =>
        await fetch(`http://localhost:8080/admin/users/update`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(user),
        }),
    deleteUser: async (id) =>
        await fetch(`http://localhost:8080/admin/user/delete?id=${id}`, {
            method: 'POST',
            headers: headers,
        }),
    getAllRoles: async () => await fetch('http://localhost:8080/admin/roles'),
};

document.addEventListener('DOMContentLoaded', () => {
    initializeViews();
    loadRoles();
    getTableWithUsers();
    setupAddUserForm();
});

function initializeViews() {
    const adminButton = document.getElementById('adminButton');
    const userButton = document.getElementById('userButton');
    const adminView = document.getElementById('adminView');
    const userView = document.getElementById('userView');

    // Функция для переключения активного состояния кнопок
    const setActiveButton = (activeButton) => {
        adminButton.classList.remove('active');
        userButton.classList.remove('active');
        activeButton.classList.add('active');
    };

    adminButton.addEventListener('click', () => {
        adminView.classList.remove('d-none');
        userView.classList.add('d-none');
        setActiveButton(adminButton);
    });

    userButton.addEventListener('click', () => {
        adminView.classList.add('d-none');
        userView.classList.remove('d-none');  // Отображаем текст для пользователя
        setActiveButton(userButton);
    });

    // Устанавливаем активное состояние для кнопки "Admin" по умолчанию
    setActiveButton(adminButton);
}

async function getTableWithUsers() {
    const tableBody = document.querySelector('#mainTableWithUsers tbody');
    tableBody.innerHTML = ''; // Clear existing rows

    try {
        const response = await userFetchService.findAllUsers();
        const users = await response.json();

        users.forEach(user => {
            const roles = user.roles.map(role => role.name).join(', ');
            const row = `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${roles}</td>
                    <td>
                        <button class="btn btn-info" onclick="editUser(${user.id})">Edit</button>
                    </td>
                    <td>
                        <button class="btn btn-danger" onclick="deleteUser(${user.id})">Delete</button>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

async function setupAddUserForm() {
    const addButton = document.getElementById('addNewUserButton');
    const form = document.getElementById('defaultSomeForm');

    addButton.addEventListener('click', async () => {
        const user = {
            firstName: document.getElementById('AddNewUserFirstName').value.trim(),
            lastName: document.getElementById('AddNewUserLastName').value.trim(),
            age: Number(document.getElementById('AddNewUserAge').value.trim()),
            email: document.getElementById('AddNewUserEmail').value.trim(),
            password: document.getElementById('AddNewUserPassword').value.trim(),
            passwordConfirm: document.getElementById('AddNewUserConfirmPassword').value.trim(),
            roles: Array.from(document.getElementById('AddNewUserRoles').selectedOptions).map(opt => ({ name: opt.value }))
        };

        const response = await userFetchService.addNewUser(user);

        if (response.ok) {
            form.reset(); // Очистка формы
            getTableWithUsers(); // Обновление таблицы
            alert('User added successfully');
            document.querySelector('#usersTableTab').click(); // Переход на вкладку таблицы
        } else {
            const error = await response.json();
            alert(`Failed to add user:\n${error.message}`);
        }
    });
}


async function editUser(userId) {
    // Получение элемента модального окна (Bootstrap 5)
    const modalElement = document.getElementById('editUserModal');
    const modal = new bootstrap.Modal(modalElement);

    // Получаем данные пользователя
    const userResponse = await userFetchService.findOneUser(userId);
    const user = await userResponse.json();

    // Заполняем форму модального окна
    document.getElementById('editUserId').value = user.id;
    document.getElementById('editUserFirstName').value = user.firstName;
    document.getElementById('editUserLastName').value = user.lastName;
    document.getElementById('editUserAge').value = user.age;
    document.getElementById('editUserEmail').value = user.email;
    document.getElementById('editUserPassword').value = '';
    document.getElementById('editUserPasswordConfirm').value = '';

    // Загружаем роли
    const rolesResponse = await userFetchService.getAllRoles();
    const roles = await rolesResponse.json();
    const rolesSelect = document.getElementById('editUserRoles');

    rolesSelect.innerHTML = ''; // Очищаем текущие опции
    roles.forEach(role => {
        const isSelected = user.roles.some(r => r.name === role.name);
        const option = new Option(role.name, role.id, isSelected, isSelected);
        rolesSelect.add(option);
    });

    // Показываем модальное окно
    modal.show();

    // Обработчик на кнопку "Edit" в модальном окне
    document.getElementById('saveEditUserButton').onclick = async () => {
        // Собираем данные из формы
        const editedUser = {
            id: parseInt(document.getElementById('editUserId').value),
            firstName: document.getElementById('editUserFirstName').value.trim(),
            lastName: document.getElementById('editUserLastName').value.trim(),
            age: parseInt(document.getElementById('editUserAge').value.trim()),
            email: document.getElementById('editUserEmail').value.trim(),
            password: document.getElementById('editUserPassword').value.trim(),
            passwordConfirm: document.getElementById('editUserPasswordConfirm').value.trim(),
            roles: Array.from(rolesSelect.selectedOptions).map(option => ({
                id: parseInt(option.value),
                name: option.text
            }))
        };

        // Отправляем данные на сервер
        const response = await userFetchService.updateUser(editedUser);
        if (response.ok) {
            getTableWithUsers();
            modal.hide();
            alert('User updated successfully');
        } else {
            const error = await response.json();
            alert(`Failed to update user:\n${error.message}`);
        }
    };
}


async function deleteUser(userId) {
    // Получение элемента модального окна (Bootstrap 5)
    const modalElement = document.getElementById('deleteUserModal');
    const modal = new bootstrap.Modal(modalElement);

    // Получаем данные пользователя
    const userResponse = await userFetchService.findOneUser(userId);
    const user = await userResponse.json();

    // Заполняем форму модального окна
    document.getElementById('deleteUserId').value = user.id;
    document.getElementById('deleteUserFirstName').value = user.firstName;
    document.getElementById('deleteUserLastName').value = user.lastName;
    document.getElementById('deleteUserAge').value = user.age;
    document.getElementById('deleteUserEmail').value = user.email;

    // Загружаем роли
    const roles = user.roles.map(role => role.name).join(', ');
    document.getElementById('deleteUserRoles').value = roles;

    // Показываем модальное окно
    modal.show();

    // Обработчик на кнопку "Delete" в модальном окне
    document.getElementById('deleteUserButton').onclick = async () => {
        const response = await userFetchService.deleteUser(userId);
        if (response.ok) {
            getTableWithUsers();
            modal.hide();
            alert('User deleted successfully');
        } else {
            alert('Failed to delete user');
        }
    };
}


async function loadRoles() {
    const rolesSelect = document.getElementById('AddNewUserRoles');
    rolesSelect.innerHTML = ''; // Clear existing options

    try {
        const response = await userFetchService.getAllRoles();
        const roles = await response.json();

        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.name;
            option.textContent = role.name;
            rolesSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading roles:', error);
    }
}


