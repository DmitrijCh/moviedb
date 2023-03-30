'use strict';

// переключение между формой регистрации и авторизацией
function showRegistration() {
    const reg = document.getElementById('registration');
    const login = document.getElementById('authorization');
    if (reg.style.display === 'none') {
        reg.style.display = 'block';
        login.style.display = 'none';
    }
}

// Регистрация
// форма регистрации
function registrationForm() {
    const hello = document.getElementById('hello-user');
    const registration = document.getElementById('registration');
    if (hello.style.display === 'block') {
        hello.style.display = 'none';
    } else {
        hello.style.display = 'block';
        registration.style.display = 'none';
    }
}

// отправка форы регистрации и получение "ключа"
function registration() {
    const regForm = document.getElementById('registration-form');
    regForm.addEventListener('submit', async function (e) {
        registrationForm();
        e.preventDefault();
        const load = new FormData(regForm);

        const response = await fetch('http://localhost:9000/register', {
            method: 'POST',
            body: load,
            headers: {Accept: 'application/json'},
        })
        const json = await response.json();
        console.log(json);
        localStorage.setItem('key', json.key);
        if (localStorage.getItem('key') !== null) {
            await verificationUser(json.key);
            await verificationMovie(json.key);
        }
    });
}

// Авторизация
// форма авторизации
function authorizationForm() {
    const hello = document.getElementById("hello-user");
    const authorization = document.getElementById('authorization');
    if (hello.style.display === 'block') {
        hello.style.display = 'none';
    } else {
        hello.style.display = 'block';
        authorization.style.display = 'none';
    }
}

// отправка форы авторизации и получение "ключа"
function authorization() {
    const authForm = document.getElementById('authorization-form');
    authForm.addEventListener('submit', async function (e) {
        authorizationForm();
        e.preventDefault();
        const load = new FormData(authForm);

        const response = await fetch('http://localhost:9000/logins', {
            method: 'POST',
            body: load,
            headers: {Accept: 'application/json'},
        })
        const json = await response.json();
        console.log(json);
        localStorage.setItem('key', json.key);
        if (localStorage.getItem('key') !== null) {
            await verificationUser(json.key);
            await verificationMovie(json.key);
        }
    });
}

// Ключ-сессии
// принимает ключ, проверяет данные пользователя, после отправляет приветсвие пользователю
async function verificationUser(key) {
    let paramKey = new FormData();
    paramKey.set('key', key);

    const response = await fetch('http://localhost:9000/message/user', {
        method: 'POST',
        body: paramKey
    })
    const json = await response.json();
    console.log(json.message);
    document.getElementById('hello-user').innerHTML = json.message +
        document.getElementById('hello-user').innerHTML;
    outputButton();
}

// отправляет список фильмов пользователю
async function verificationMovie() {
    const response = await fetch('http://localhost:9000/message/movie', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
    });
    const movies = await response.json();
    console.log(movies);
    movies.forEach(movie => {
        const movieDiv = document.createElement('div');
        const name = document.createElement('h2');
        const year = document.createElement('p');
        const poster = document.createElement('img');

        name.textContent = movie.name;
        year.textContent = movie.year;
        poster.src = movie.poster;

        movieDiv.appendChild(name);
        movieDiv.appendChild(year);
        movieDiv.appendChild(poster);

        document.getElementById('hello-user').appendChild(movieDiv);
    });

    outputButton();
}

// если в localStorage есть ключ - блок регистрации или авторизации сменяется блоком приветствия пользователя
function presenceKey() {
    if (localStorage.getItem('key') !== null) {
        const hello = document.getElementById('hello-user');
        const authorization = document.getElementById('authorization');
        if (hello.style.display === 'block') {
            hello.style.display = 'none';
        } else {
            hello.style.display = 'block';
            authorization.style.display = 'none';
            void verificationUser(localStorage.getItem('key'));
            void verificationMovie(localStorage.getItem('key'));
        }
    }
}

// кнопка отвечающая за переключение между формой регистрации и авторизацией
function authorizationButton() {
    document.getElementById('auth-button').addEventListener('click', showRegistration);
}

// кнопка "Регистрация"
function registrationButton() {
    document.getElementById('reg-button').addEventListener('click', registration)
}

// кнопка "Вход"
function loginButton() {
    document.getElementById('login-button').addEventListener('click', authorization);
}

// кнопка "Выход" со страницы пользователя
function outputButton() {
    document.getElementById('output-button').addEventListener('click', () => {
        localStorage.removeItem('key');
        location.reload();
    })
}

function main() {
    authorizationButton();
    registrationButton();
    loginButton();
    presenceKey();
}

main();


