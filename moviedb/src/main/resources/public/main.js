function filling_form() {
    const form = document.getElementById('form')
    let fail = false;
    let name = form.name.value;
    let login = form.login.value;
    let password = form.password.value;

    if (name === '' || name === ' ') {
        fail = 'Вы не ввели свое имя';
    } else if (login === '') {
        fail = 'Вы не ввели логин';
    } else if (password === '') {
        fail = 'Вы не ввели пароль';
    } else if (password.length <= 6) {
        fail = "Пароль должен содержать не менее 6-ти символов!"
    }

    if (fail) {
        alert(fail);
    } else {
        const user_element = document.getElementById("hello-user");
        const register_element = document.getElementById('reg');
        if (user_element.style.display === 'block') {
            user_element.style.display = 'none';
        } else {
            user_element.style.display = 'block';
            register_element.style.display = 'none';
        }
    }
}

function login(id) {
    const id_element = document.getElementById(id);
    const login_element = document.getElementById('log');
    if (id_element.style.display === 'block') {
        id_element.style.display = 'none';
    } else {
        id_element.style.display = 'block';
        login_element.style.display = 'none';
    }
}

function saved() {
    if (localStorage.getItem('key') !== null) {
        const hello_element = document.getElementById('hello-user');
        const login_element = document.getElementById('log');
        if (hello_element.style.display === 'block') {
            hello_element.style.display = 'none';
        } else {
            hello_element.style.display = 'block';
            login_element.style.display = 'none';
            registerUser(localStorage.getItem('key'))
        }
    }
}

saved()

function registration() {
    const form = document.getElementById('form');
    form.addEventListener('submit', function (e) {
        filling_form();
        e.preventDefault();
        const load = new FormData(form);

        fetch('http://localhost:9000/register', {
            method: 'POST',
            body: load,
            headers: {Accept: 'application/json'},
        })
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                console.log(data);
                localStorage.setItem('key', data.key);
                savedUser(data.key);
            })
    });
}

function registerUser(key) {
    let param = new FormData();
    param.set('key', key)

    fetch('http://localhost:9000/message', {
        method: 'POST',
        body: param
    })
        .then(function (response) {
            return response.json();
        })
        .then(function (response) {
            console.log(response.message);
            document.getElementById('hello-user').innerHTML = response.message +
                document.getElementById('hello-user').innerHTML;
            output_button()
        })
}

function savedUser(key) {
    if (localStorage.getItem('key') !== null) {
        registerUser(key)
    }
}

function reg_button() {
    document.getElementById('reg-button').addEventListener('click', () => {
        registration();
    })
}

reg_button();

function log_button() {
    document.getElementById('login').addEventListener('click', () => {
        login('reg')
    })
}

log_button()

function output_button() {
    document.getElementById('output-button').addEventListener('click', () => {
        localStorage.removeItem('key');
        location.reload();
    })
}