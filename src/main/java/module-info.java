module com.andr3yqq.vulcanoeruptiongame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.andr3yqq.vulcanoeruptiongame to javafx.fxml;
    exports com.andr3yqq.vulcanoeruptiongame;
}