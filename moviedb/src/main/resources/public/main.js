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
    const descriptionMovies = document.getElementById('movie-details');
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

    if (descriptionMovies.style.display === 'block') {
        descriptionMovies.style.display = 'none';
    } else {
        descriptionMovies.style.display = 'block';
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
    const descriptionMovies = document.getElementById('movie-details');
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

    if (descriptionMovies.style.display === 'block') {
        descriptionMovies.style.display = 'none';
    } else {
        descriptionMovies.style.display = 'block';
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
            await userFavoriteMovies(localStorage.getItem('key'));
            await favoriteMovies(localStorage.getItem('key'))
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
            void userFavoriteMovies(localStorage.getItem('key'));
            void userMovieRating(localStorage.getItem('key'))
            void allMovieRating(localStorage.getItem('key'))
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

    // проверяем наличие переменной для кнопки "Добавить еще"
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
function createMovieElement(movie, key, consoleOutput) {
    const movieDiv = document.createElement("div");
    const id = document.createElement("p");
    const name = document.createElement("h2");
    const year = document.createElement("p");
    const time = document.createElement("p");
    const description = document.createElement("p");
    const slogan = document.createElement("p");
    const age = document.createElement("p");
    const budget = document.createElement("p");
    const country = document.createElement("p");
    const type = document.createElement("p");
    const poster = document.createElement("img");
    const likeButton = document.createElement("button");
    const dislikeButton = document.createElement("button");
    const descriptionButton = document.createElement("button");
    const starButton = document.createElement("button");
    const favoriteLabel = document.createElement("span");
    const timeDiv = document.createElement("div");
    const rating = document.createElement("p");
    const allRating = document.createElement("p");

    id.textContent = movie.id;
    name.textContent = movie.name;
    year.textContent = movie.year;
    time.textContent = movie.time;
    slogan.textContent = movie.slogan;
    age.textContent = movie.age;
    budget.textContent = movie.budget;
    country.textContent = movie.country;
    type.textContent = movie.type;
    poster.src = movie.poster;
    // rating.textContent = movie.rating

    id.style.display = "none";
    time.style.display = "none";
    description.style.display = "none"
    slogan.style.display = "none"
    age.style.display = "none"
    budget.style.display = "none"
    country.style.display = "none"
    type.style.display = "none"

    descriptionButton.style.width = "95px";
    descriptionButton.style.marginTop = "10px";
    descriptionButton.textContent = "Описание";
    descriptionButton.style.marginRight = "10px";

    likeButton.style.width = "65px";
    likeButton.style.marginTop = "10px";
    likeButton.style.marginRight = "10px";
    likeButton.textContent = "👍";

    dislikeButton.style.width = "65px";
    dislikeButton.style.marginTop = "10px";
    dislikeButton.style.marginRight = "10px";
    dislikeButton.style.display = "none";
    dislikeButton.textContent = "👎";

    starButton.style.width = "55px";
    starButton.style.marginTop = "10px";
    starButton.textContent = "⭐️";

    favoriteLabel.textContent = "Фильм добавлен в избранное!";
    favoriteLabel.style.display = "none";
    favoriteLabel.style.fontSize = "18px";

    rating.style.fontSize = "18px";

    allRating.style.fontSize = "18px";

    // добавление фильма в избранное
    likeButton.addEventListener("click", async () => {
        let paramKey = new FormData();
        paramKey.set("key", key);
        paramKey.set("movieID", movie.id);

        const response = await fetch("http://localhost:9000/message/movie/like", {
            method: "POST",
            body: paramKey,
        });

        likeButton.style.display = "none"; // скрываем кнопку "Нравится"
        dislikeButton.style.display = "inline"; // отображаем кнопку "Дизлайк"
        favoriteLabel.style.display = "inline"; // отображаем метку "Добавлено в избранное"
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

        dislikeButton.style.display = "none"; // скрываем элемент при удалении из избранного
        favoriteLabel.style.display = "none";
        likeButton.style.display = "inline";
        favoriteLabel.style.display = "none";
    });

    // описание фильмов
    descriptionButton.addEventListener("click", () => {

        const movieDescription = "Описание фильма:";

        document.getElementById("movie-name").textContent = movie.name;

        const movieYearLabel = document.createElement("span");
        movieYearLabel.textContent = "Год производства: ";
        const movieYearValue = document.createElement("span");
        movieYearValue.textContent = movie.year;
        document.getElementById("movie-year").innerHTML = "";
        document.getElementById("movie-year").appendChild(movieYearLabel);
        document.getElementById("movie-year").appendChild(movieYearValue);

        const movieTimeLabel = document.createElement("span");
        movieTimeLabel.textContent = "Продолжительность: ";
        const movieTimeValue = document.createElement("span");
        movieTimeValue.textContent = movie.time;
        document.getElementById("movie-time").innerHTML = "";
        document.getElementById("movie-time").appendChild(movieTimeLabel);
        document.getElementById("movie-time").appendChild(movieTimeValue);

        document.getElementById("movie-desc").textContent = movie.description;

        const movieSloganLabel = document.createElement("span");
        movieSloganLabel.textContent = "Слоган: ";
        const movieSloganValue = document.createElement("span");
        movieSloganValue.textContent = movie.slogan;
        document.getElementById("movie-slogan").innerHTML = "";
        document.getElementById("movie-slogan").appendChild(movieSloganLabel);
        document.getElementById("movie-slogan").appendChild(movieSloganValue);

        const movieAgeLabel = document.createElement("span");
        movieAgeLabel.textContent = "Возраст: ";
        const movieAgeValue = document.createElement("span");
        movieAgeValue.textContent = movie.age;
        document.getElementById("movie-age").innerHTML = "";
        document.getElementById("movie-age").appendChild(movieAgeLabel);
        document.getElementById("movie-age").appendChild(movieAgeValue);

        const movieBudgetLabel = document.createElement("span");
        movieBudgetLabel.textContent = "Бюджет: ";
        const movieBudgetValue = document.createElement("span");
        movieBudgetValue.textContent = movie.budget;
        document.getElementById("movie-budget").innerHTML = "";
        document.getElementById("movie-budget").appendChild(movieBudgetLabel);
        document.getElementById("movie-budget").appendChild(movieBudgetValue);

        const movieCountryLabel = document.createElement("span");
        movieCountryLabel.textContent = "Страна: ";
        const movieCountryValue = document.createElement("span");
        movieCountryValue.textContent = movie.country;
        document.getElementById("movie-country").innerHTML = "";
        document.getElementById("movie-country").appendChild(movieCountryLabel);
        document.getElementById("movie-country").appendChild(movieCountryValue);

        const movieTypeLabel = document.createElement("span");
        movieTypeLabel.textContent = "Тип: ";
        const movieTypeValue = document.createElement("span");
        movieTypeValue.textContent = movie.type;
        document.getElementById("movie-type").innerHTML = "";
        document.getElementById("movie-type").appendChild(movieTypeLabel);
        document.getElementById("movie-type").appendChild(movieTypeValue);

        document.getElementById("movie-poster").src = movie.poster;

        console.log(movieDescription);

        // отобразить описание фильма
        document.getElementById("movie-description").textContent = movieDescription;
        document.getElementById("movie-details").style.display = "block";

        // скрываем все блоки на странице, кроме блока с описанием фильма
        const allBlocks = document.querySelectorAll('body > div:not(#movie-details)');
        allBlocks.forEach(block => block.style.display = 'none');
    });

    // рейтинг фильма
    starButton.addEventListener("click", async () => {
        const movieRating = "рейтинг:";

        const popupContainer = document.createElement("div");
        popupContainer.classList.add("popup-container");

        if (!document.getElementById("buttons-wrapper")) {
            const buttonsWrapper = document.createElement("div");
            buttonsWrapper.classList.add("buttons-wrapper");
            buttonsWrapper.id = "buttons-wrapper";

            for (let i = 0; i <= 10; i++) {
                const ratingButton = document.createElement("button");
                ratingButton.textContent = i;
                ratingButton.addEventListener("click", async () => {
                    console.log("Выбранный рейтинг:", i);

                    let paramRating = new FormData();
                    paramRating.set("key", key);
                    paramRating.set("movieID", movie.id);
                    paramRating.set("rating", i);

                    const ratingResponse = await fetch("http://localhost:9000/message/movie/rating", {
                        method: "POST",
                        body: paramRating,
                    });
                    popupContainer.style.display = "none";
                    overlay.style.display = "none";
                });
                buttonsWrapper.appendChild(ratingButton);
            }
            popupContainer.appendChild(buttonsWrapper);
        }
        popupContainer.classList.add("popup-container");

        const overlay = document.createElement("div");
        overlay.classList.add("overlay");

        movieDiv.appendChild(overlay);
        movieDiv.appendChild(popupContainer);
    });

    // получение рейтинга фильма и присваивание его элементу rating
    userMovieRating(key)
        .then(movieRatings => {
            const userRating = movieRatings.find(movieRating => movieRating.id === movie.id);
            if (userRating) {
                rating.textContent = `Ваша оценка: ${userRating.rating}`;
            }
        })
        .catch(error => {
            console.error("Ошибка при получении рейтинга фильма:", error);
        });

    // получение рейтинга фильма и присваивание его элементу rating
    allMovieRating(key)
        .then(movieRatings => {
            const allUserRating = movieRatings.find(movieRating => movieRating.id === movie.id);
            if (allUserRating) {
                allRating.textContent = `Средняя оценка: ${allUserRating.rating}`;
            }
        })
        .catch(error => {
            console.error("Ошибка при получении рейтинга фильма:", error);
        });

    // получение понравившегося фильма пользователю
    userFavoriteMovies(key)
        .then(consoleOutputs => {
            const userFavorite = consoleOutputs.find(consoleOutput => consoleOutput.id === movie.id);
            if (userFavorite) {
                likeButton.style.display = "none"; // скрываем кнопку "Нравится"
                dislikeButton.style.display = "inline"; // отображаем кнопку "Дизлайк"
                favoriteLabel.style.display = "inline"; // отображаем метку "Добавлено в избранное"
            } else {
                likeButton.style.display = "inline"; // отображаем кнопку "Нравится"
                dislikeButton.style.display = "none"; // скрываем кнопку "Дизлайк"
                favoriteLabel.style.display = "none"; // скрываем метку "Добавлено в избранное"
            }
        })
        .catch(error => {
            console.error("Ошибка при получении статуса фильма:", error);
        });


    // добавление элементов в контейнер div
    const buttonsDiv = document.createElement("div");
    buttonsDiv.appendChild(descriptionButton);
    buttonsDiv.appendChild(likeButton);
    buttonsDiv.appendChild(dislikeButton);
    buttonsDiv.appendChild(starButton);

    movieDiv.appendChild(id);
    movieDiv.appendChild(name);
    movieDiv.appendChild(year);
    movieDiv.appendChild(time);
    movieDiv.appendChild(description);
    movieDiv.appendChild(slogan);
    movieDiv.appendChild(age);
    movieDiv.appendChild(budget);
    movieDiv.appendChild(country);
    movieDiv.appendChild(type);
    movieDiv.appendChild(poster);
    movieDiv.appendChild(buttonsDiv);
    movieDiv.appendChild(favoriteLabel);
    movieDiv.appendChild(timeDiv);
    movieDiv.appendChild(rating);
    movieDiv.appendChild(allRating);

    return movieDiv;
}

// получаем избранные фильмы от пользователя
async function userFavoriteMovies(key) {
    const formData = new FormData();
    formData.append('key', key);

    const response = await fetch('http://localhost:9000/message/movie/inform', {
        method: 'POST',
        body: formData,
        headers: {'Accept': 'application/json'}
    });

    const json = await response.json();
    return json;
}

// получаем рейтинг от пользователя
async function userMovieRating(key) {
    let paramRating = new FormData();
    paramRating.set("key", key);

    const response = await fetch(`http://localhost:9000/message/movie/user_rating`, {
        method: 'POST',
        body: paramRating,
        headers: {'Accept': 'application/json'}
    });

    const json = await response.json();
    return json;
}

// получаем общий рейтинг
async function allMovieRating() {
    const response = await fetch('http://localhost:9000/message/movie/all_rating', {
        method: 'GET',
        headers: {'Accept': 'application/json'}
    });

    const json = await response.json();
    return json;
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
    searchMovies();
}

main();


