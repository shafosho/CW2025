package com.comp2042.gameLogic;

import com.comp2042.data.DownData;
import com.comp2042.data.MoveEvent;
import com.comp2042.data.ViewData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    void createNewGame();
}
