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

// если в localStorage есть ключ - блок регистрации или авторизации сменяется блоком приветствия пользователя
function presenceKey() {
    const movies = document.getElementById('search-movies');

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

        movies.style.display = 'block';
    } else {
        movies.style.display = 'none';
    }
}

// отправляет список фильмов пользователю, выводим фильмы по нажатию кнопки
let loadedMoviesCount = 0;
let loadMoreButton = null;

async function verificationMovie(key) {
    let paramKey = new FormData();
    paramKey.set("key", key);

    const response = await fetch("http://localhost:9000/message/movie", {
        method: "POST",
        body: paramKey,
    });

    const movies = await response.json();
    console.log(movies);

    const newMovies = movies.slice(loadedMoviesCount, loadedMoviesCount + 16);

    for (let i = 0; i < newMovies.length; i++) {
        const movie = newMovies[i];
        const movieDiv = createMovieElement(movie);
        document.getElementById("movie").appendChild(movieDiv);
    }

    loadedMoviesCount += newMovies.length;

    // Проверяем наличие переменной для кнопки "Добавить еще"
    if (!loadMoreButton) {
        // если переменная отсутствует, создаем кнопку "Добавить еще"
        loadMoreButton = document.createElement("button");
        loadMoreButton.style.textAlign = "center";
        loadMoreButton.style.height = "50px";
        loadMoreButton.style.width = "1400px";
        loadMoreButton.textContent = "Добавить еще";

        // добавляем обработчик события на нажатие кнопки
        loadMoreButton.addEventListener("click", () => {
            verificationMovie(key);
        });
    }

    // создаем элемент div для кнопки "Добавить еще"
    const loadMoreDiv = document.createElement("div");
    loadMoreDiv.style.textAlign = "center";

    // добавьте кнопку "Добавить еще" в элемент div
    loadMoreDiv.appendChild(loadMoreButton);

    // добавьте элемент div к родительскому элементу списка фильмов.
    document.getElementById("movie").parentNode.appendChild(loadMoreDiv);
}

// Собираем все элементы фильма
function createMovieElement(movie) {
    const movieDiv = document.createElement("div");
    const name = document.createElement("h2");
    const year = document.createElement("p");
    const poster = document.createElement("img");
    const likeButton = document.createElement("button");

    name.textContent = movie.name;
    year.textContent = movie.year;
    poster.src = movie.poster;
    poster.style.display = "block";
    likeButton.style.display = "block";
    likeButton.style.marginTop = "10px";
    likeButton.style.width = "140px";
    likeButton.textContent = "Нравится";

    likeButton.addEventListener("click", (key, value) => {
        localStorage.setItem(`liked_${movie.name}`, value);
        alert("Фильм добавлен в избранное!");
    });

    movieDiv.appendChild(name);
    movieDiv.appendChild(year);
    movieDiv.appendChild(poster);
    movieDiv.appendChild(likeButton);

    return movieDiv;
}

/* отправляет фильм по поиску пользователю, поиск показывает все фильмы который содержит одинаковую строку,
 также не учитываться регистр
 */
function searchMovies() {
    const searchBar = document.getElementById('search-bar');
    const searchButton = document.getElementById('search-button');
    const searchResults = document.getElementById('search-results');

    searchButton.addEventListener('click', async () => {
        searchResults.innerHTML = '';

        const formData = new FormData();
        formData.append('name', searchBar.value);

        const response = await fetch('http://localhost:9000/search', {
            method: 'POST',
            body: formData
        });

        const movies = await response.json();

        if (movies.length === 0) {
            searchResults.innerHTML = 'Ничего не нашлось';
        } else {
            movies.forEach(movie => {
                if (new RegExp(searchBar.value, 'i').test(movie.name)) {
                    const movieSearch = document.createElement('div');
                    const name = document.createElement('h2');
                    const year = document.createElement('p');
                    const poster = document.createElement('img');

                    name.textContent = movie.name;
                    year.textContent = movie.year;
                    poster.src = movie.poster;

                    movieSearch.appendChild(name);
                    movieSearch.appendChild(year);
                    movieSearch.appendChild(poster);

                    searchResults.appendChild(movieSearch);
                }
            });
        }
    });
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
    searchMovies();
}

main();


