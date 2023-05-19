package ru.yandex.practicum.filmorate.constants;

public class Constants {

    public static final String USERS_PATH = "/users";
    public static final String RECEIVE_GET_REQUEST = "Получен запрос GET";
    public static final String CURRENT_USER_COUNT = "Текущее количество пользователей: {}";
    public static final String RECEIVE_POST_REQUEST = "Получен запрос POST";
    public static final String RECEIVED_EMPTY_REQUEST = "Получен пустой запрос";
    public static final String ADDED_NEW_USER = "Добавлен новый пользователь с Id: ";
    public static final String RECEIVED_PUT_REQUEST = "Получен запрос PUT";
    public static final String UPDATE_USER = "Обновлен пользователь с Id: ";
    public static final String USER_HAS_NO_ID = "Не указан Id пользователя";
    public static final String PLEASE_FIX_AND_TRY_AGAIN = "Укажите и попробуйте снова";
    public static final String USER_HAS_NO_EMAIL = "Не указана электронная почта либо неверный формат почты";
    public static final String USER_HAS_NO_LOGIN = "Пустой логин";
    public static final String NEW_USER_EMPTY_NAME_REPLACED_BY_LOGIN = "Новому пользователю передано пустое имя - будет заменено логином";
    public static final String USER_BIRTHDAY_IN_FUTURE = "Дата рождения не может быть в будущем";
    public static final String FILMS_PATH = "/films";
    public static final String CURRENT_FILM_COUNT = "Текущее количество фильмов: {}";
    public static final String ADDED_NEW_FILM = "Добавлен новый фильм с Id: ";
    public static final String FILM_HAS_NO_ID = "Не указан Id фильма";
    public static final String UPDATED_FILM = "Обновлен фильм с Id: ";
    public static final String FILM_HAS_NO_NAME = "Не указано название фильма";
    public static final String FILM_MAX_DESCRIPTION_LENGTH = "Максимальная длина описания - 200 символов. Или пустая";
    public static final String FILM_HAS_NO_RELEASE_DATE = "Дата релиза не указана";
    public static final String FILM_INVALID_RELEASE_DATE = "Дата релиза — не может быть раньше 28 декабря 1895 года.";
    public static final String FILM_DURATION_INVALID = "Продолжительность фильма должна быть положительной";
    public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, \n" +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. \n" +
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco aboris " +
            "nisi ut aliquip ex ea commodo consequat.";
    public static final String NEW_FILM_NOT_FOUND = "Новый фильм не найден";
    public static final String RECEIVED_FILM_AND_NEW_FILM_DIFFERENT = "Отправленный и добавленный фильм отличаются";
    public static final String NEW_USER_NOT_FOUND = "Новый пользователь не найден";
    public static final String RECEIVED_AND_NEW_USER_DIFFERENT = "Отправленный и добавленный пользователь отличаются";
}