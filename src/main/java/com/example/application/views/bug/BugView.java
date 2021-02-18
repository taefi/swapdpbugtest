package com.example.application.views.bug;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.application.views.main.MainView;

@PageTitle("Bug")
@Route(value = "bug", layout = MainView.class)
public class BugView extends Div {

    public BugView() {
        add(new ComboBoxContainer());
        add(new GridContainer());
    }

    public static class MyPojo {
        private final Div div;

        public MyPojo(String name) {
            this.div = new Div(new Text(name));
        }

        public Div getDiv() {
            div.getElement().getNode().setParent(null);
            return div;
        }
    }

    public static class ComboBoxContainer extends Div {
        public ComboBoxContainer () {
            ComboBox<MyPojo> comboBox = new ComboBox<>();
            comboBox.setId("combo-box");

            ListDataProvider<MyPojo> dataProvider = new ListDataProvider<>(
                    IntStream.range(0, 10).mapToObj(i -> new MyPojo(" Data " + i))
                            .collect(Collectors.toList()));
            comboBox.setDataProvider(dataProvider);
            comboBox.setRenderer(new ComponentRenderer<>(MyPojo::getDiv));

            add(comboBox);

            Button button = new Button("Reset data provider", e -> {
                remove(comboBox);
                //comboBox.setDataProvider(dataProvider);
                addComponentAsFirst(comboBox);
            });
            button.setId("combo-related-btn");
            add(button);
        }
    }

    public static class GridContainer extends Div {
        public GridContainer () {
            Grid<MyPojo> grid = new Grid<>();

            ListDataProvider<MyPojo> dataProvider = new ListDataProvider<>(
                    IntStream.range(0, 10).mapToObj(i -> new MyPojo(" Data " + i))
                            .collect(Collectors.toList()));
            grid.setDataProvider(dataProvider);
            grid.addColumn(new ComponentRenderer<>(MyPojo::getDiv));

            add(grid);
            add(new Button("Reset data provider", e -> {
                remove(grid);
                grid.setDataProvider(dataProvider);
                addComponentAsFirst(grid);
            }));
        }
    }
}