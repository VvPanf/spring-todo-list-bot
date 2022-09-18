package com.vvpanf.todolistbot.model.handler;

import com.vvpanf.todolistbot.entity.Item;
import com.vvpanf.todolistbot.entity.TodoList;
import com.vvpanf.todolistbot.entity.User;
import com.vvpanf.todolistbot.model.BotStateCash;
import com.vvpanf.todolistbot.model.constants.BotMenuCommand;
import com.vvpanf.todolistbot.model.constants.BotState;
import com.vvpanf.todolistbot.service.TodoListService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventHandler {
    final TodoListService todoListService;
    final BotStateCash botStateCash;

    final String CLOSE_SIGN = Character.toString(0x0000274C);
    final String CHECKED_SIGN = Character.toString(0x00002705);
    final String UNCHECKED_SIGN = Character.toString(0x00002B55);
    final String NEXT_SIGN = Character.toString(0x000025B6);
    final String PREV_SIGN = Character.toString(0x000025C0);

    final int PAGE_SIZE = 5;
    final int MAX_LISTS = 10;
    final int MAX_ITEMS = 50;

    /**
     * Приветственное сообщение бота
     * @param chatId
     * @param userId
     * @return
     */
    public List<BotApiMethod<?>> startMessage(long chatId, long userId) {
        todoListService.addUser(userId);
        String text = "*Вас приветствоет бот для составления списков*\n" +
                     "Версия 1.0\n" +
                     "/show - посмотреть свои списки\n" +
                     "/create - создать новый список\n" +
                     "/delete - удалить список\n" +
                     "/help - справка";
        return List.of(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .parseMode("Markdown")
                .build());
    }

    /**
     * Список команд бота
     * @param chatId
     * @return
     */
    public List<BotApiMethod<?>> helpMessage(long chatId) {
        String text = "*Список команд*\n" +
                     "/show - посмотреть свои списки\n" +
                     "/create - создать новый список\n" +
                     "/delete - удалить список\n" +
                     "/help - справка";
        return List.of(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .parseMode("Markdown")
                .build());
    }

    /**
     * Показывает все списки для пользователя
     * @param chatId
     * @param userId
     * @return
     */
    public List<BotApiMethod<?>> showListsMessage(long chatId, long userId) {
        String text = "Ваши списки:";
        List<TodoList> todoLists = todoListService.getLists(userId);
        if (todoLists.isEmpty()) {
            text = "У вас нет созданных списков.";
            return List.of(new SendMessage(String.valueOf(chatId), text));
        }
        List<List<InlineKeyboardButton>> keyboardButtons = todoLists.stream().map(todoList -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(todoList.getName());
            button.setCallbackData("list" + todoList.getId().toString());
            return List.of(button);
        }).collect(Collectors.toList());
        addMenuButtons(keyboardButtons, false);
        return List.of(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(new InlineKeyboardMarkup(keyboardButtons))
                .build());
    }

    /**
     * Переход в состояние ввода списка
     * @param chatId
     * @param userId
     * @return
     */
    public List<BotApiMethod<?>> createListMessage(long chatId, long userId) {
        String text = "Введите название списка и его элементы (первая строчка - название, далее - элементы на каждой строчке)";
        saveBotState(userId, BotState.ENTER_LIST);
        return List.of(new SendMessage(String.valueOf(chatId), text));
    }

    /**
     * Ввод заголовка и элементов списка
     * @param chatId
     * @param userId
     * @param inputText
     * @return
     */
    public List<BotApiMethod<?>> enterListMessage(long chatId, long userId, String inputText) {
        final List<String> headerAndItems = Arrays.asList(inputText.split("\n"));
        String text = "";
        if (headerAndItems.size() == 0) {
            text = "Неверно введён список";
        } else if (headerAndItems.size() == 1) {
            text = "Введён только заголовок списка";
        } else if (headerAndItems.size() > MAX_ITEMS + 1) {
            text = "В списке максимум может быть " + MAX_ITEMS + " элементов";
        } else if (todoListService.getLists(userId).size() >= MAX_LISTS) {
            text = "У пользователя может быть максимум " + MAX_LISTS + "списков";
        } else {
            String header = headerAndItems.get(0);
            List<String> items = headerAndItems.subList(1, headerAndItems.size());
            todoListService.addList(userId, header, items);
            text = "Список добавлен";
        }
        saveBotState(userId, BotState.MAIN);
        return List.of(new SendMessage(String.valueOf(chatId), text));
    }

    /**
     * Показ списков пользователя для удаления
     * @param chatId
     * @return
     */
    public List<BotApiMethod<?>> deleteListMessage(long chatId, long userId) {
        String text = "Выберите список для удаления";
        List<TodoList> todoLists = todoListService.getLists(userId);
        if (todoLists.isEmpty()) {
            text = "У вас нет созданных списков";
            return List.of(new SendMessage(String.valueOf(chatId), text));
        }
        List<List<InlineKeyboardButton>> keyboardButtons = todoLists.stream().map(todoList -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(todoList.getName());
            button.setCallbackData("delete" + todoList.getId().toString());
            return List.of(button);
        }).collect(Collectors.toList());
        addMenuButtons(keyboardButtons, false);
        return List.of(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(new InlineKeyboardMarkup(keyboardButtons))
                .build());
    }

    /**
     * Сообщение в случае неизвестной команды
     * @param chatId
     * @return
     */
    public List<BotApiMethod<?>> unknownMessage(long chatId) {
        String text = "Неизвестная команда";
        return List.of(new SendMessage(String.valueOf(chatId), text));
    }

    /**
     * Удаляет сообщение
     * @param chatId
     * @param messageId
     * @return
     */
    public List<BotApiMethod<?>> closeCallback(long chatId, long messageId, String callbackId) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        result.add(new AnswerCallbackQuery(callbackId));
        result.add(new DeleteMessage(String.valueOf(chatId), (int) messageId));
        return result;
    }

    /**
     * Показывает следующую страницу
     * @param chatId
     * @param userId
     * @param messageId
     * @param messageText
     * @param callbackId
     * @return
     */
    public List<BotApiMethod<?>> prevCallback(long chatId, long userId, long messageId, String messageText, String callbackId) {
        return pageCallback(chatId, userId, messageId, messageText, callbackId, false);
    }

    /**
     * Показывает предыдущую страницу
     * @param chatId
     * @param userId
     * @param messageId
     * @param messageText
     * @param callbackId
     * @return
     */
    public List<BotApiMethod<?>> nextCallback(long chatId, long userId, long messageId, String messageText, String callbackId) {
        return pageCallback(chatId, userId, messageId, messageText, callbackId, true);
    }

    /**
     * Показывает элементы списка
     * @param chatId
     * @param userId
     * @param listId
     * @return
     */
    public List<BotApiMethod<?>> showListItemsCallback(long chatId, long userId, long messageId, long listId) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        TodoList todoList = todoListService.getList(listId);
        if (!todoList.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Wrong user");
        }

        int listSize = todoList.getItems().size();
        String text = todoList.getName() + "\n" + "1/" + getLastPage(listSize);

        List<List<InlineKeyboardButton>> keyboardButtons = buttonsFromListItems(todoList, 1);
        addMenuButtons(keyboardButtons, listSize > PAGE_SIZE);

        result.add(DeleteMessage.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .build());
        result.add(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(new InlineKeyboardMarkup(keyboardButtons))
                .build());
        return result;
    }

    /**
     * Изменяет значение галочки в списке
     * @param chatId
     * @param userId
     * @param messageId
     * @param itemId
     * @return
     */
    public List<BotApiMethod<?>> toggleItemCallback(long chatId, long userId, long messageId, String messageText, long itemId, String callbackId) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        Item item = todoListService.getItem(itemId);
        TodoList todoList = item.getList();
        if (!todoList.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Wrong user");
        }

        item.setChecked(!item.isChecked());
        todoListService.saveItem(item);

        int currentPage = Integer.parseInt(messageText.split("\n")[1].split("/")[0]);
        int listSize = todoList.getItems().size();
        List<List<InlineKeyboardButton>> keyboardButtons = buttonsFromListItems(todoList, currentPage);
        addMenuButtons(keyboardButtons, listSize > PAGE_SIZE);

        result.add(new AnswerCallbackQuery(callbackId));
        result.add(EditMessageReplyMarkup.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .replyMarkup(new InlineKeyboardMarkup(keyboardButtons))
                .build());
        return result;
    }

    /**
     * Удаляет список
     * @param chatId
     * @param userId
     * @param deleteId
     * @return
     */
    public List<BotApiMethod<?>> deleteListCallback(long chatId, long userId, long messageId, long deleteId) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        String text = "Список успешно удалён";
        todoListService.removeList(userId, deleteId);

        result.add(DeleteMessage.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .build());
        result.add(new SendMessage(String.valueOf(chatId), text));
        return result;
    }

    private void saveBotState(long userId, BotState nextState) {
        botStateCash.saveBotState(userId, nextState);
    }

    private void addMenuButtons(List<List<InlineKeyboardButton>> keyboardButtons, boolean isFull) {
        if (isFull) {
            InlineKeyboardButton btnPrev = new InlineKeyboardButton();
            btnPrev.setText(PREV_SIGN);
            btnPrev.setCallbackData(BotMenuCommand.PREV.toString());

            InlineKeyboardButton btnNext = new InlineKeyboardButton();
            btnNext.setText(NEXT_SIGN);
            btnNext.setCallbackData(BotMenuCommand.NEXT.toString());

            InlineKeyboardButton btnClose = new InlineKeyboardButton();
            btnClose.setText(CLOSE_SIGN);
            btnClose.setCallbackData(BotMenuCommand.CLOSE.toString());

            keyboardButtons.addAll(List.of(List.of(btnPrev, btnClose, btnNext)));
        } else {
            InlineKeyboardButton btnClose = new InlineKeyboardButton();
            btnClose.setText(CLOSE_SIGN + "Закрыть");
            btnClose.setCallbackData(BotMenuCommand.CLOSE.toString());
            keyboardButtons.add(List.of(btnClose));
        }
    }

    private List<List<InlineKeyboardButton>> buttonsFromListItems(TodoList todoList, int currentPage) {
        return todoList.getItems().stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .skip((currentPage-1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .map(listItem -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    String checkSign = listItem.isChecked()
                            ? CHECKED_SIGN
                            : UNCHECKED_SIGN;
                    button.setText(checkSign + listItem.getContent());
                    button.setCallbackData("item" + listItem.getId().toString());
                    return List.of(button);
                }).collect(Collectors.toList());
    }

    private List<BotApiMethod<?>> pageCallback(long chatId, long userId, long messageId, String messageText, String callbackId, boolean isNext) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        User user = todoListService.getUser(userId);
        String listName = messageText.split("\n")[0];
        TodoList todoList = user.getLists().stream().filter(list -> list.getName().equals(listName)).findFirst().orElseThrow();

        int listSize = todoList.getItems().size();
        int currentPage = Integer.parseInt(messageText.split("\n")[1].split("/")[0]);
        currentPage += isNext ? 1 : -1;
        if (currentPage > getLastPage(listSize)) {
            currentPage = 1;
        }
        if (currentPage <= 0) {
            currentPage = getLastPage(listSize);
        }
        String text = todoList.getName() + "\n" + currentPage + "/" + getLastPage(listSize);

        List<List<InlineKeyboardButton>> keyboardButtons = buttonsFromListItems(todoList, currentPage);
        addMenuButtons(keyboardButtons, listSize > PAGE_SIZE);

        result.add(new AnswerCallbackQuery(callbackId));
        result.add(EditMessageReplyMarkup.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .replyMarkup(new InlineKeyboardMarkup(keyboardButtons))
                .build());
        result.add(EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .text(text)
                .build());
        return result;
    }

    private int getLastPage(int listSize) {
        return (int) Math.ceil((double) listSize / (double) PAGE_SIZE);
    }
}
