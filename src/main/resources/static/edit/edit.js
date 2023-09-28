// Function to fetch and display the user list in a table
function fetchUsers() {
    fetch('http://localhost:8081/api/users/') // Địa chỉ URL API của bạn
        .then(response => response.json())
        .then(users => {
            const tableBody = document.getElementById('userList');
            tableBody.innerHTML = ''; // Xóa nội dung tbody hiện có

            users.forEach(user => {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = user.id;
                const nameCell = document.createElement('td');
                nameCell.textContent = user.name;
                const emailCell = document.createElement('td');
                emailCell.textContent = user.email;
                const roleCell = document.createElement('td');
                roleCell.textContent = user.role;

                row.appendChild(idCell);
                row.appendChild(nameCell);
                row.appendChild(emailCell);
                row.appendChild(roleCell);

                const actionsCell = document.createElement('td');
                const editBtn = document.createElement('button');
                editBtn.textContent = 'Edit';
                editBtn.className = 'edit-btn';
                actionsCell.appendChild(editBtn);

                const deleteBtn = document.createElement('button');
                deleteBtn.textContent = 'Delete';
                deleteBtn.className = 'delete-btn';
                actionsCell.appendChild(deleteBtn);

                row.appendChild(actionsCell);

                tableBody.appendChild(row);

                // Sự kiện click cho nút "Edit"
                editBtn.addEventListener('click', () => {
                    const editedNameInput = document.getElementById('newName-Edit');
                    const editedEmailInput = document.getElementById('newEmail-Edit');

                    editedNameInput.value = user.name;
                    editedEmailInput.value = user.email;

                    // Hiển thị popup để chỉnh sửa người dùng
                    const editPopup = document.getElementById('popup-Edit');
                    editPopup.style.display = 'block';

                    // Sự kiện submit cho form chỉnh sửa người dùng
                    const editUserForm = document.getElementById('userForm-Edit');
                    editUserForm.addEventListener('submit', async (event) => {
                        event.preventDefault();

                        const editedName = editedNameInput.value;
                        const editedEmail= editedEmailInput.value;
                        // const editedPassword = editedPasswordInput.value;

                        if (editedName !== '' && editedEmail !== '') {
                            const data = {
                                name: editedName,
                                email: editedEmail,
                                // password: editedPassword,
                            };
                            try {
                                const response = await fetch(`http://localhost:8081/api/users/${user.id}`, {
                                    method: 'PUT',
                                    headers: {
                                        'Content-Type': 'application/json',
                                    },
                                    body: JSON.stringify(data),
                                });

                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                const updatedUser = await response.json();
                                nameCell.textContent = updatedUser.name;
                                emailCell.textContent = updatedUser.email;
                                alert('Cập nhật thành công');

                                // Đóng popup sau khi cập nhật thành công
                                editPopup.style.display = 'none';
                                // Tải lại trang sau khi cập nhật thành công
                                window.location.reload();
                            } catch (error) {
                                console.error('Lỗi khi cập nhật: ' + error.message);
                                alert('Lỗi khi cập nhật');
                            }
                        } else {
                            alert('Vui lòng điền đầy đủ thông tin');
                        }
                    });
                });


                deleteBtn.addEventListener('click', () => {
                    const userId = user.id;
                    const confirmDelete = confirm('Bạn có chắc chắn muốn xóa người dùng này?');
                    if (confirmDelete) {
                        fetch(`http://localhost:8081/api/users/${userId}`, {
                            method: 'DELETE',
                        })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                return response.text();
                            })
                            .then(responseText => {
                                row.remove();
                                alert('Xóa thành công');
                                // Tải lại trang sau khi cập nhật thành công
                                window.location.reload();
                            })
                            .catch(error => {
                                console.error('Lỗi khi xóa: ' + error.message);
                                alert('Lỗi khi xóa');
                            });
                    }
                });

            });

            const createUserBtn = document.getElementById('createUserBtn');
            const popup = document.getElementById('popup');
            const userForm = document.getElementById('userForm');
            const closePopupBtn = document.getElementById('closePopupBtn');

            createUserBtn.addEventListener('click', () => {
                // Hiển thị popup để tạo người dùng
                popup.style.display = 'block';
            });

            closePopupBtn.addEventListener('click', () => {
                // Đóng popup để tạo người dùng
                popup.style.display = 'none';
            });

            userForm.addEventListener('submit', async (event) => {
                event.preventDefault();

                const newName = document.getElementById('newName').value;
                const newEmail = document.getElementById('newEmail').value;
                const newPassword = document.getElementById('newPassword').value;
                if (newEmail !== '' && newName !== '' && newPassword !== '') {
                    const data = {
                        name: newName,
                        email: newEmail,
                        password: newPassword,
                    };
                    try {
                        const response = await fetch('http://localhost:8081/api/users/', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify(data),
                        });

                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }

                        const newUser = await response.json();
                        alert('Tạo mới người dùng thành công');
                        fetchUsers();
                        // Đóng popup sau khi tạo người dùng thành công
                        popup.style.display = 'none';
                        // Tải lại trang sau khi cập nhật thành công
                        window.location.reload();
                    } catch (error) {
                        console.error('Lỗi khi tạo mới người dùng: ' + error.message);
                        alert('Lỗi khi tạo mới người dùng');
                    }
                } else {
                    alert('Vui lòng điền đầy đủ thông tin');
                }
            });
        })
        .catch(error => {
            console.error('Lỗi: ' + error.message);
        });
}

// Fetch and display the initial user list
fetchUsers();




