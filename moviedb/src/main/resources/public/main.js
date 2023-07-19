'use strict';

// Переключение между формой регистрации и авторизацией
function showRegistration() {
    const reg = document.getElementById('registration');
    const login = document.getElementById('authorization');
    if (reg.style.display === 'none') {
        reg.style.display = 'block';
        login.style.display = 'none';
    }
}

// Регистрация (форма регистрации)
function registrationForm() {
    const hello = document.getElementById('hello-user');
    const movies = document.getElementById('search-movies');
    const likeMovies = document.getElementById('favorites-movies');
    const typeMovies = document.getElementById('movie-types');
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

    if (typeMovies.style.display === 'block') {
        typeMovies.style.display = 'none';
    } else {
        typeMovies.style.display = 'block';
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

// Отправка форы регистрации и получение "ключа"
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

// Авторизация (форма авторизации)
function authorizationForm() {
    const hello = document.getElementById("hello-user");
    const movies = document.getElementById('search-movies');
    const likeMovies = document.getElementById('favorites-movies');
    const typeMovies = document.getElementById('movie-types');
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

    if (typeMovies.style.display === 'block') {
        typeMovies.style.display = 'none';
    } else {
        typeMovies.style.display = 'block';
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

// Отправка форы авторизации и получение "ключа"
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
        }
    });
}

// Ключ-сессии - принимает ключ, проверяет данные пользователя, после отправляет приветсвие пользователю
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

// Если в localStorage есть ключ - блок регистрации или авторизации сменяется блоком приветствия пользователя
function presenceKey() {
    const authorization = document.getElementById('authorization');
    const hello = document.getElementById('hello-user');
    const movies = document.getElementById('search-movies');
    const likeMovies = document.getElementById('favorites-movies');
    const typeMovies = document.getElementById('movie-types');

    if (localStorage.getItem('key') !== null) {
        if (hello.style.display === 'block') {
            hello.style.display = 'none';
            movies.style.display = 'none';
            likeMovies.style.display = 'none';
            typeMovies.style.display = 'none';
        } else {
            hello.style.display = 'block';
            movies.style.display = 'block';
            likeMovies.style.display = 'block';
            typeMovies.style.display = 'block';
            authorization.style.display = 'none';
            void verificationUser(localStorage.getItem('key'));
            void verificationMovie(localStorage.getItem('key'));
            void favoriteMovies(localStorage.getItem('key'));
            void userFavoriteMovies(localStorage.getItem('key'));
            void userMovieRating(localStorage.getItem('key'))
            void allMovieRating(localStorage.getItem('key'))
            void ping(localStorage.getItem('key'))
        }
    }
}

// Отправляет список фильмов пользователю, выводим фильмы по нажатию кнопки
let loadedMoviesCount = 0;
let loadMoreButton = null;
let typeMov = "";
let typeGen = "";

document.getElementById("movie-type-select").addEventListener("change", function () {
    typeMov = this.options[this.selectedIndex].value;
    loadedMoviesCount = 0;
    document.getElementById("movie").innerHTML = "";
    void verificationMovie(key);
});

window.addEventListener("load", function () {
    const selectElement = document.getElementById("movie-type-select");
    selectElement.selectedIndex = 0;
    typeMov = selectElement.options[0].value;
    loadedMoviesCount = 0;
    document.getElementById("movie").innerHTML = "";
    void verificationMovie(key);
});

document.getElementById("movie-genres-select").addEventListener("change", function () {
    typeGen = this.options[this.selectedIndex].value;
    loadedMoviesCount = 0;
    document.getElementById("movie").innerHTML = "";
    void verificationMovie(key);
});

window.addEventListener("load", function () {
    const selectElement = document.getElementById("movie-genres-select");
    selectElement.selectedIndex = 0;
    typeGen = selectElement.options[0].value;
    loadedMoviesCount = 0;
    document.getElementById("movie").innerHTML = "";
    void verificationMovie(key);
});

async function verificationMovie(key) {
    if (!typeMov) {
        typeMov = "all";
    }

    let paramKey = new FormData();
    paramKey.set("key", key);
    paramKey.set("count", 16);
    paramKey.set("offset", loadedMoviesCount);
    paramKey.set("type", typeMov);
    paramKey.set("genres", typeGen);


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

    if (!loadMoreButton) {
        loadMoreButton = document.createElement("button");
        loadMoreButton.style.textAlign = "center";
        loadMoreButton.style.height = "50px";
        loadMoreButton.style.width = "1400px";
        loadMoreButton.textContent = "Добавить еще";

        loadMoreButton.addEventListener("click", () => {
            verificationMovie(key);
        });
    }
    const loadMoreDiv = document.createElement("div");
    loadMoreDiv.style.textAlign = "center";
    loadMoreDiv.appendChild(loadMoreButton);

    document.getElementById("movie").parentNode.appendChild(loadMoreDiv);
}

// Собираем все элементы фильма, добавляем понравившиеся фильмы в избранное, так же удаляем фильмы из избранного
function createMovieElement(movie, key) {
    const movieDiv = document.createElement("div");
    const buttonsDiv = document.createElement("div");
    const buttonDiv = document.createElement("div");
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
    const persons = document.createElement("p");
    const genres = document.createElement("p");
    const poster = document.createElement("img");
    const likeButton = document.createElement("button");
    const dislikeButton = document.createElement("button");
    const descriptionButton = document.createElement("a");
    const starButton = document.createElement("button");
    const favoriteLabel = document.createElement("span");
    const timeDiv = document.createElement("div");
    const rating = document.createElement("p");
    const allRating = document.createElement("p");
    const commentButton = document.createElement("button");

    id.textContent = movie.id;
    name.textContent = movie.name;
    year.textContent = movie.year;
    time.textContent = movie.time;
    slogan.textContent = movie.slogan;
    age.textContent = movie.age;
    budget.textContent = movie.budget;
    country.textContent = movie.country;
    type.textContent = movie.type;
    persons.textContent = movie.persons;
    genres.textContent = movie.genre;
    poster.src = movie.poster;
    // rating.textContent = movie.rating

    id.style.display = "none";
    year.style.display = "none";
    time.style.display = "none";
    description.style.display = "none"
    slogan.style.display = "none"
    age.style.display = "none"
    budget.style.display = "none"
    country.style.display = "none"
    type.style.display = "none"
    persons.style.display = "none"

    buttonDiv.style.display = "inline-flex";
    buttonDiv.style.flexDirection = "column";
    buttonDiv.style.justifyContent = "flex-start";
    buttonDiv.style.marginLeft = "10px";
    buttonDiv.style.position = "relative";
    buttonDiv.style.top = "-155px";

    buttonsDiv.style.marginTop = "-140px";

    descriptionButton.style.width = "80px";
    descriptionButton.style.marginTop = "10px";
    descriptionButton.textContent = "Описание";
    descriptionButton.style.marginRight = "10px";
    descriptionButton.style.textDecoration = "underline";
    descriptionButton.style.fontSize = "24px";

    likeButton.style.width = "55px";
    likeButton.style.marginTop = "10px";
    likeButton.style.marginRight = "10px";
    likeButton.textContent = "👍";

    dislikeButton.style.width = "55px";
    dislikeButton.style.marginTop = "10px";
    dislikeButton.style.marginRight = "10px";
    dislikeButton.style.display = "none";
    dislikeButton.textContent = "👎";

    starButton.style.width = "55px";
    starButton.style.marginTop = "10px";
    starButton.style.marginRight = "10px";
    starButton.textContent = "⭐️";

    commentButton.style.width = "55px";
    commentButton.style.marginTop = "10px";
    commentButton.textContent = "✍️";

    favoriteLabel.textContent = "Фильм добавлен в избранное!";
    favoriteLabel.style.display = "none";
    favoriteLabel.style.fontSize = "18px";

    rating.style.fontSize = "18px";
    allRating.style.fontSize = "18px";

    // Добавление фильма в избранное
    likeButton.addEventListener("click", async () => {
        let paramKey = new FormData();
        paramKey.set("key", key);
        paramKey.set("movieID", movie.id);

        const response = await fetch("http://localhost:9000/message/movie/like", {
            method: "POST",
            body: paramKey,
        });

        likeButton.style.display = "none";
        dislikeButton.style.display = "inline";
        favoriteLabel.style.display = "inline";
    });

    // Удаление фильма из избранного
    dislikeButton.addEventListener("click", async () => {
        let paramKey = new FormData();
        paramKey.set("key", key);
        paramKey.set("movieID", movie.id);

        const response = await fetch("http://localhost:9000/message/movie/dislike", {
            method: "POST",
            body: paramKey,
        });

        dislikeButton.style.display = "none";
        favoriteLabel.style.display = "none";
        likeButton.style.display = "inline";
        favoriteLabel.style.display = "none";
    });

    // Описание фильма
    descriptionButton.addEventListener("click", () => {

        const movieDescription = "Описание фильма:";

        document.getElementById("movie-name").textContent = movie.name;

        const movieYearLabel = document.createElement("span");
        movieYearLabel.textContent = "Год производства: ";
        movieYearLabel.style.color = '#006400';
        const movieYearValue = document.createElement("span");
        movieYearValue.textContent = movie.year;
        movieYearValue.style.color = '#006400';
        document.getElementById("movie-year").innerHTML = "";
        document.getElementById("movie-year").appendChild(movieYearLabel);
        document.getElementById("movie-year").appendChild(movieYearValue);

        const movieTimeLabel = document.createElement("span");
        movieTimeLabel.textContent = "Продолжительность: ";
        movieTimeLabel.style.color = '#006400';
        const movieTimeValue = document.createElement("span");
        movieTimeValue.textContent = movie.time;
        movieTimeValue.style.color = '#006400';
        document.getElementById("movie-time").innerHTML = "";
        document.getElementById("movie-time").appendChild(movieTimeLabel);
        document.getElementById("movie-time").appendChild(movieTimeValue);

        document.getElementById("movie-desc").textContent = movie.description;

        const moviePersonsLabel = document.createElement("span");
        moviePersonsLabel.textContent = "Актеры: ";
        moviePersonsLabel.style.color = '#006400';
        const moviePersonsValue = document.createElement("span");
        moviePersonsValue.textContent = movie.persons;
        moviePersonsValue.style.color = '#006400';
        document.getElementById("movie-persons").innerHTML = "";
        document.getElementById("movie-persons").appendChild(moviePersonsLabel);
        document.getElementById("movie-persons").appendChild(moviePersonsValue);

        const movieSloganLabel = document.createElement("span");
        movieSloganLabel.textContent = "Слоган: ";
        movieSloganLabel.style.color = '#006400';
        const movieSloganValue = document.createElement("span");
        movieSloganValue.textContent = movie.slogan;
        movieSloganValue.style.color = '#006400';
        document.getElementById("movie-slogan").innerHTML = "";
        document.getElementById("movie-slogan").appendChild(movieSloganLabel);
        document.getElementById("movie-slogan").appendChild(movieSloganValue);

        const movieAgeLabel = document.createElement("span");
        movieAgeLabel.textContent = "Возраст: ";
        movieAgeLabel.style.color = "#006400";
        const movieAgeValue = document.createElement("span");
        movieAgeValue.textContent = movie.age;
        movieAgeValue.style.color = "#006400";
        document.getElementById("movie-age").innerHTML = "";
        document.getElementById("movie-age").appendChild(movieAgeLabel);
        document.getElementById("movie-age").appendChild(movieAgeValue);

        const movieBudgetLabel = document.createElement("span");
        movieBudgetLabel.textContent = "Бюджет: ";
        movieBudgetLabel.style.color = "#006400";
        const movieBudgetValue = document.createElement("span");
        movieBudgetValue.textContent = movie.budget;
        movieBudgetValue.style.color = "#006400";
        document.getElementById("movie-budget").innerHTML = "";
        document.getElementById("movie-budget").appendChild(movieBudgetLabel);
        document.getElementById("movie-budget").appendChild(movieBudgetValue);

        const movieCountryLabel = document.createElement("span");
        movieCountryLabel.textContent = "Страна: ";
        movieCountryLabel.style.color = "#006400";
        const movieCountryValue = document.createElement("span");
        movieCountryValue.textContent = movie.country;
        movieCountryValue.style.color = "#006400";
        document.getElementById("movie-country").innerHTML = "";
        document.getElementById("movie-country").appendChild(movieCountryLabel);
        document.getElementById("movie-country").appendChild(movieCountryValue);

        const movieTypeLabel = document.createElement("span");
        movieTypeLabel.textContent = "Тип: ";
        movieTypeLabel.style.color = "#006400";
        const movieTypeValue = document.createElement("span");
        movieTypeValue.textContent = movie.type;
        movieTypeValue.style.color = "#006400";
        document.getElementById("movie-type").innerHTML = "";
        document.getElementById("movie-type").appendChild(movieTypeLabel);
        document.getElementById("movie-type").appendChild(movieTypeValue);

        const movieGenreLabel = document.createElement("span");
        movieGenreLabel.textContent = "Тип: ";
        movieGenreLabel.style.color = "#006400";
        const movieGenreValue = document.createElement("span");
        movieGenreValue.textContent = movie.genres;
        movieGenreValue.style.color = "#006400";
        document.getElementById("movie-genres").innerHTML = "";
        document.getElementById("movie-genres").appendChild(movieGenreLabel);
        document.getElementById("movie-genres").appendChild(movieGenreValue);

        document.getElementById("movie-poster").src = movie.poster;

        console.log(movieDescription);

        document.getElementById("movie-description").textContent = movieDescription;
        document.getElementById("movie-details").style.display = "block";
        document.getElementById("movie-details").style.color = "#006400";

        const allBlocks = document.querySelectorAll('body > div:not(#movie-details)');
        allBlocks.forEach(block => block.style.display = 'none');
    });

    // Рейтинг фильма
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

    // Возможность пользователю оставить комментарий
    commentButton.addEventListener('click', async function () {
        document.body.innerHTML = '';

        const commentForm = document.createElement('form');
        const commentsDiv = document.createElement('div');
        const commentsHeader = document.createElement('h2');
        const userLoginRow = document.createElement('p');
        const existingComments = document.createElement('div');
        const commentTextarea = document.createElement('textarea');
        const publishButton = document.createElement('button');

        commentsHeader.textContent = 'Оставьте свой отзыв';
        commentsHeader.style.color = '#006400';
        publishButton.textContent = 'Опубликовать';
        publishButton.style.width = '150px';
        commentsDiv.style.display = 'flex';
        commentsDiv.style.flexDirection = 'column';
        commentsDiv.style.alignItems = 'center';
        commentsDiv.style.color = '#006400';
        commentForm.style.display = 'flex';
        commentForm.style.flexDirection = 'column';
        commentForm.style.alignItems = 'center';
        commentTextarea.rows = 15;
        commentTextarea.cols = 70;

        const movieID = movie.id;

        const comments = await loadComments(movieID);

        comments.forEach(comment => {
            const loginElement = document.createElement('p');
            loginElement.textContent = comment.userLogin;
            loginElement.style.marginTop = "50px";
            existingComments.appendChild(loginElement);

            const commentElement = document.createElement('p');
            commentElement.textContent = comment.comment;
            commentElement.style.marginLeft = '30px';
            existingComments.appendChild(commentElement);
        });

        commentForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const formData = new FormData();
            formData.append('key', key);
            formData.append('movieID', movie.id);
            formData.append('comment', commentTextarea.value);

            const response = await fetch('http://localhost:9000/message/movie/comment', {
                method: 'POST',
                body: formData,
            });

            const newComment = document.createElement("p");
            newComment.textContent = commentTextarea.value;
            existingComments.appendChild(newComment);

            commentTextarea.value = "";
        });

        commentsDiv.appendChild(commentsHeader);
        commentsDiv.appendChild(commentTextarea);
        commentsDiv.appendChild(commentForm);
        commentsDiv.appendChild(existingComments);
        commentForm.appendChild(publishButton);
        commentForm.appendChild(userLoginRow);

        document.body.appendChild(commentsDiv);
    });

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

    userFavoriteMovies(key)
        .then(consoleOutputs => {
            const userFavorite = consoleOutputs.find(consoleOutput => consoleOutput.id === movie.id);
            if (userFavorite) {
                likeButton.style.display = "none";
                dislikeButton.style.display = "inline";
                favoriteLabel.style.display = "inline";
            } else {
                likeButton.style.display = "inline";
                dislikeButton.style.display = "none";
                favoriteLabel.style.display = "none";
            }
        })
        .catch(error => {
            console.error("Ошибка при получении статуса фильма:", error);
        });

    buttonDiv.appendChild(poster);
    buttonDiv.appendChild(likeButton);
    buttonDiv.appendChild(dislikeButton);
    buttonDiv.appendChild(starButton);
    buttonDiv.appendChild(commentButton);
    buttonsDiv.appendChild(descriptionButton);

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
    movieDiv.appendChild(persons);
    movieDiv.appendChild(genres);
    movieDiv.appendChild(poster);
    movieDiv.appendChild(buttonDiv);
    movieDiv.appendChild(buttonsDiv);
    movieDiv.appendChild(favoriteLabel);
    movieDiv.appendChild(timeDiv);
    movieDiv.appendChild(rating);
    movieDiv.appendChild(allRating);

    return movieDiv;
}

// Получаем избранные фильмы от пользователя
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

// Получаем рейтинг от пользователя
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

// Получаем общий рейтинг
async function allMovieRating() {
    const response = await fetch('http://localhost:9000/message/movie/all_rating', {
        method: 'GET',
        headers: {'Accept': 'application/json'}
    });

    const json = await response.json();
    return json;
}

// Получаем комментарии пользователей
async function loadComments(movieID) {
    const formData = new FormData();
    formData.append('movieID', movieID);

    const response = await fetch('http://localhost:9000/message/movie/all_comments', {
        method: 'POST',
        body: formData,
        headers: {'Accept': 'application/json'}
    });

    const json = await response.json();
    return json;
}

// Выводим фильмы пользователя из "избранного"
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

        favoritesResults.innerHTML = '';

        movies.forEach(movie => {
            const movieFavorite = document.createElement('div');
            const name = document.createElement('h2');
            const poster = document.createElement('img');

            name.textContent = movie.name;
            poster.src = movie.poster;

            movieFavorite.appendChild(name);
            movieFavorite.appendChild(poster);

            favoritesResults.appendChild(movieFavorite);
        });

        const allBlocks = document.querySelectorAll('body > div:not(#favorites-movies)');
        allBlocks.forEach(block => block.style.display = 'none');
    });
}

/* Отправляет фильм по поиску пользователю, поиск показывает все фильмы который содержит одинаковую строку,
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

        const allBlocks = document.querySelectorAll('body > div:not(#search-movies)');
        allBlocks.forEach(block => block.style.display = 'none');
    });
}

// Пользователь отправляет пинг, что он онлайн
async function ping(key) {
    const formData = new FormData();
    formData.append('key', key);

    const response = await fetch('http://localhost:9000/ping', {
        method: 'POST',
        body: formData,
        headers: {'Accept': 'application/json'}
    });

    const count = await response.text();
    const label = count === '1' ? 'пользователь онлайн' : 'пользователя(-ей) онлайн';

    const onlineUserDiv = document.getElementById('online-user');
    onlineUserDiv.style.display = 'flex';
    onlineUserDiv.style.justifyContent = 'center';
    onlineUserDiv.style.alignItems = 'center';
    onlineUserDiv.style.color = '#006400';
    onlineUserDiv.style.fontSize = '30px';

    onlineUserDiv.innerHTML = `${count} ${label}`;

    return count;
}

setInterval(async () => {
    const key = localStorage.getItem('key');
    const response = await ping(key);
    console.log(response);
}, 5000);

// Кнопка отвечающая за переключение между формой регистрации и авторизацией
function authorizationButton() {
    document.getElementById('auth-button').addEventListener('click', showRegistration);
}

// Кнопка "Регистрация"
function registrationButton() {
    document.getElementById('reg-button').addEventListener('click', registration)
}

// Кнопка "Вход"
function loginButton() {
    document.getElementById('login-button').addEventListener('click', authorization);
}

// Кнопка "Выход" со страницы пользователя
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
