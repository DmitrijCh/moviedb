function login(id) { //переключение между формой регистрации и авторизацией ????? //toggleReg
    const idElement = document.getElementById(id);
    const login = document.getElementById('authorization');
    if (idElement.style.display === 'block') {
        idElement.style.display = 'none';
    } else {
        idElement.style.display = 'block';
        login.style.display = 'none';
    }
}

function authorizationButton() { //кнопка отвечающая за переключение между формой регистрации и авторизацией ?????
    document.getElementById('auth-button').addEventListener('click', () => {
        login('registration');
    })
}

authorizationButton();

//Регистрация
function registrationForm() { //форма регистрации
    const hello = document.getElementById("hello-user");
    const registration = document.getElementById('registration');
    if (hello.style.display === 'block') {
        hello.style.display = 'none';
    } else {
        hello.style.display = 'block';
        registration.style.display = 'none';
    }
}

function registration() { //отправка форы регистрации и получение "ключа"
    const regForm = document.getElementById('registration-form');
    regForm.addEventListener('submit', function (e) {
        registrationForm();
        e.preventDefault();
        const load = new FormData(regForm);

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
                if (localStorage.getItem('key') !== null) {
                    verificationUser(data.key);
                }
            })
    });
}

function registrationButton() { //кнопка "Регистрация"
    document.getElementById('reg-button').addEventListener('click', () => {
        registration();
    })
}

registrationButton();

//Авторизация
function authorizationForm() { //форма авторизации
    const forms = document.getElementById('authorization-form')
    let logins = forms.login.value;
    let passwords = forms.password.value;

    const hello = document.getElementById("hello-user");
    const authorization = document.getElementById('authorization');
    if (logins && passwords === localStorage.getItem('key')) {
        if (hello.style.display === 'block') {
            hello.style.display = 'none';
        } else {
            hello.style.display = 'block';
            authorization.style.display = 'none';
        }
    }
}

function authorization() { //отправка форы авторизации и получение "ключа"
    const authForm = document.getElementById('authorization-form');
    authForm.addEventListener('submit', function (e) {
        authorizationForm();
        e.preventDefault();
        const load = new FormData(authForm);

        fetch('http://localhost:9000/logins', {
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
                if (localStorage.getItem('key') !== null) {
                    verificationUser(data.key);
                    location.reload();
                }
            })
    });
}

function loginButton() { //кнопка "Вход"
    document.getElementById('login-button').addEventListener('click', () => {
        authorization();
    })
}

loginButton();

//Ключ-сессии
function verificationUser(key) { //принимает ключ, проверяет данные пользователя, после отправляет приветсвие пользователю
    let paramKey = new FormData();
    paramKey.set('key', key);

    fetch('http://localhost:9000/message', {
        method: 'POST',
        body: paramKey
    })
        .then(function (response) {
            return response.json();
        })
        .then(function (response) {
            console.log(response.message);
            document.getElementById('hello-user').innerHTML = response.message +
                document.getElementById('hello-user').innerHTML;
            outputButton();
        })
}

function savedKey() { //если в localStorage есть ключ - блок регистрации или авторизации сменяется блоком приветствия пользователя ?????
    if (localStorage.getItem('key') !== null) {
        const hello = document.getElementById('hello-user');
        const authorization = document.getElementById('authorization');
        if (hello.style.display === 'block') {
            hello.style.display = 'none';
        } else {
            hello.style.display = 'block';
            authorization.style.display = 'none';
            verificationUser(localStorage.getItem('key'));
        }
    }
}

savedKey();

function outputButton() { //кнопка "Выход" со страницы пользователя
    document.getElementById('output-button').addEventListener('click', () => {
        localStorage.removeItem('key');
        location.reload();
    })
}