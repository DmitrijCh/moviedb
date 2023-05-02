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
    const movies = document.getElementById('search-movies');
    const likeMovies = document.getElementById('favorites-movies');
    const registration = document.getElementById('registration');
    if (hello.style.display === 'block') {
        hello.style.display = 'none';
    } else {
        hello.style.display = 'block';
        registration.style.display = 'none';
    }

    if (likeMovies.style.display === 'block') {
        likeMovies.style.display = 'none';
    } else {
        likeMovies.style.display = 'block';
        registration.style.display = 'none';
    }

    if (movies.style.display === 'block') {
        movies.style.display = 'none';
    } else {
        movies.style.display = 'block';
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
    const movies = document.getElementById('search-movies');
    const likeMovies = document.getElementById('favorites-movies');
    const authorization = document.getElementById('authorization');
    if (hello.style.display === 'block') {
        hello.style.display = 'none';
    } else {
        hello.style.display = 'block';
        authorization.style.display = 'none';
    }

    if (likeMovies.style.display === 'block') {
        likeMovies.style.display = 'none';
    } else {
        likeMovies.style.display = 'block';
        authorization.style.display = 'none';
    }

    if (movies.style.display === 'block') {
        movies.style.display = 'none';
    } else {
        movies.style.display = 'block';
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
            await favoriteMovies(localStorage.getItem('key'));
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
    const authorization = document.getElementById('authorization');
    const hello = document.getElementById('hello-user');
    const movies = document.getElementById('search-movies');
    const likeMovies = document.getElementById('favorites-movies');

    if (localStorage.getItem('key') !== null) {
        if (hello.style.display === 'block') {
            hello.style.display = 'none';
            movies.style.display = 'none';
            likeMovies.style.display = 'none';
        } else {
            hello.style.display = 'block';
            movies.style.display = 'block';
            likeMovies.style.display = 'block';
            authorization.style.display = 'none';
            void verificationUser(localStorage.getItem('key'));
            void verificationMovie(localStorage.getItem('key'));
            void favoriteMovies(localStorage.getItem('key'));
        }
    }
}

// отправляет список фильмов пользователю, выводим фильмы по нажатию кнопки
let loadedMoviesCount = 0;
let loadMoreButton = null;

async function verificationMovie(key) {
    let paramKey = new FormData();
    paramKey.set("key", key);
    paramKey.set("count", 16);
    paramKey.set("offset", loadedMoviesCount);

    const response = await fetch("http://localhost:9000/message/movie", {
        method: "POST",
        body: paramKey,
    });

    const movies = await response.json();
    console.log(movies);

    for (let i = 0; i < movies.length; i++) {
        const movie = movies[i];
        const movieDiv = createMovieElement(movie, key);
        document.getElementById("movie").appendChild(movieDiv);
    }

    loadedMoviesCount += movies.length;

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

// Собираем все элементы фильма, добавляем понравившиеся фильмы в избранное, так же удаляем фильмы из избранного
function createMovieElement(movie, key, id) {
    const movieDiv = document.createElement("div");
    const name = document.createElement("h2");
    const year = document.createElement("p");
    const poster = document.createElement("img");
    const likeButton = document.createElement("button");
    const dislikeButton = document.createElement("button");
    const favoriteLabel = document.createElement("span");

    name.textContent = movie.name;
    year.textContent = movie.year;
    poster.src = movie.poster;

    likeButton.style.width = "65px";
    likeButton.style.marginTop = "10px";
    likeButton.style.marginRight = "10px";
    likeButton.textContent = "👍";

    dislikeButton.style.width = "65px";
    dislikeButton.style.marginTop = "10px";
    dislikeButton.textContent = "👎";

    favoriteLabel.textContent = "Фильм добавлен в избранное!";
    favoriteLabel.style.display = "none";
    favoriteLabel.style.fontSize = "18px";

    // добавление фильма в избранное
    likeButton.addEventListener("click", async () => {
        let paramKey = new FormData();
        paramKey.set("key", key);
        paramKey.set("movieID", movie.id);

        const response = await fetch("http://localhost:9000/message/movie/like", {
            method: "POST",
            body: paramKey,
        });

        favoriteLabel.style.display = "inline";
    });

    // удаление фильма из избранного
    dislikeButton.addEventListener("click", async () => {
        let paramKey = new FormData();
        paramKey.set("key", key);
        paramKey.set("movieID", movie.id);

        const response = await fetch("http://localhost:9000/message/movie/dislike", {
            method: "POST",
            body: paramKey,
        });

        favoriteLabel.style.display = "none"; // скрываем элемент при удалении из избранного
        updateFavoriteMovies(key); // обновляем список избранных фильмов на странице
    });

    // добавляем элементы в контейнер div
    const buttonsDiv = document.createElement("div");
    buttonsDiv.appendChild(likeButton);
    buttonsDiv.appendChild(dislikeButton);

    movieDiv.appendChild(name);
    movieDiv.appendChild(year);
    movieDiv.appendChild(poster);
    movieDiv.appendChild(buttonsDiv);
    movieDiv.appendChild(favoriteLabel);

    return movieDiv;
}

// выводим фильмы пользователя из "избранного"
function favoriteMovies(key) {
    const favoritesButton = document.getElementById('favorites-button');
    const favoritesResults = document.getElementById('favorites-container');

    favoritesButton.addEventListener("click", async () => {
        let paramKey = new FormData();
        paramKey.set("key", key);

        const response = await fetch('http://localhost:9000/message/movie/favorites', {
            method: "POST",
            body: paramKey,
        });

        const movies = await response.json();

        movies.forEach(movie => {
            const movieFavorite = createMovieElement(movie, key);
            favoritesResults.appendChild(movieFavorite);
        });

        // Скрываем все блоки на странице, кроме блока с понравившимися фильмами
        const allBlocks = document.querySelectorAll('body > div:not(#favorites-movies)');
        allBlocks.forEach(block => block.style.display = 'none');
    });
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

        // Скрываем все блоки на странице, кроме блока с понравившимися фильмами
        const allBlocks = document.querySelectorAll('body > div:not(#search-movies)');
        allBlocks.forEach(block => block.style.display = 'none');
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
    favoriteMovies();
    searchMovies();
}

main();


