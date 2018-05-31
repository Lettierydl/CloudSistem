package com.cs.sis.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;

public class AutoCompleteTextField extends TextField {

    private final SortedSet<String> entries;
    private ListView<String> listView;
    private Popup popup;
    private int min_length = 3;
    private int maxHeight = 150;

    public AutoCompleteTextField() {
        super();
        entries = new TreeSet<>();
        popup = new Popup();
        popup.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
        popup.setWidth(getWidth());

        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (newValue!=null && newValue.length() >= min_length) {
                    updatePopup();
                } else {
                    popup.hide();
                }
            }
        });

        setOnMouseClicked(e -> {
            updatePopup();
        });

        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue,
                    Boolean newValue) {
                popup.hide();
            }
        });
    }

    private void updatePopup() {
        if (getText() == null) {
            return;
        } 
        LinkedList<String> searchResult = new LinkedList<>();
        searchResult.addAll(entries.stream().filter(
                x -> x.startsWith(getText())
        ).collect(Collectors.toList()));
        if (entries.size() > 0) {
            populatePopup(searchResult);
            if (!popup.isShowing()) {
                Point2D p = this.localToScene(0.0, 0.0);
                double x = p.getX() + this.getScene().getX() + this.getScene().getWindow().getX();
                double y = p.getY() + this.getScene().getY() + this.getScene().getWindow().getY() + this.getHeight();
                popup.show(AutoCompleteTextField.this, x, y);
            }
        } else {
            popup.hide();
        }
    }

    public SortedSet<String> getEntries() {
        return entries;
    }
    
    public void setList(Collection<String> list){
        entries.clear();
        entries.addAll(list);
    }
    
    public void removeItem(String item){
       entries.remove(item);
    }
    
    public void upperCaseText(){
        MaskFieldUtil.upperCase(this);
    }

    private void populatePopup(List<String> searchResult) {
        listView = new ListView<>();
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                ObservableList<String> itemSelecionado = listView.getSelectionModel().getSelectedItems();
                String selected = listView.getSelectionModel().getSelectedItem();
                setText(selected);
                popup.hide();
                positionCaret(selected.length());
            }
        });
        listView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    String selected = listView.getSelectionModel().getSelectedItem();
                    if(selected  == null || selected.isEmpty()){
                        return;
                    }
                    setText(selected);
                    popup.hide();
                    positionCaret(selected.length());
                } else if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                    popup.hide();
                    setText("");
                }
            }
        });

        listView.getItems().clear();
        int count = searchResult.size();
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            listView.getItems().add(result);
        }
        if((count*26)+10 < maxHeight){
            listView.setMaxHeight((count*26)+10);
        }else{
            listView.setMaxHeight(maxHeight);
        }
        listView.setMaxWidth(getWidth());
        
        popup.getContent().clear();
        popup.getContent().add(listView);
    }

    public int getMin_length() {
        return min_length;
    }

    public void setMin_length(int min_length) {
        this.min_length = min_length;
    }

    public int getMax_Height() {
        return maxHeight;
    }

    public void setMax_Height(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    
}
