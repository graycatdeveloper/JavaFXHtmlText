package graycatdeveloper.javafxhtmltext;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.awt.Desktop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public final class Html {

    private final ArrayList<Text> result = new ArrayList<>();
    private final HashSet<Element> elements = new HashSet<>();
    private final Options defaultOptions = new Options();

    public class Options {

        private String defaultFontFamily, defaultColor = "white";
        private int defaultFontSize = 16;
        private List<String> defaultClasses = new ArrayList<>();

        public Options setDefaultFontFamily(String defaultFontFamily) {
            this.defaultFontFamily = defaultFontFamily;
            return this;
        }

        public Options setDefaultColor(String defaultColor) {
            this.defaultColor = defaultColor;
            return this;
        }

        public Options setDefaultFontSize(int defaultFontSize) {
            this.defaultFontSize = defaultFontSize;
            return this;
        }

        public Options setDefaultClasses(String... defaultClasses) {
            this.defaultClasses = Arrays.asList(defaultClasses);
            return this;
        }
    }

    public static void set(TextFlow textFlow, String source, Options... options) {
        Html html = new Html();
        boolean o = options != null && options.length > 0;
        html.parse(Jsoup.parse(source), html.elements, html.result,
                o ? options[0].defaultClasses : html.defaultOptions.defaultClasses,
                o ? options[0].defaultFontFamily : html.defaultOptions.defaultFontFamily,
                o ? options[0].defaultColor : html.defaultOptions.defaultColor,
                o ? options[0].defaultFontSize : html.defaultOptions.defaultFontSize/*,
                background*/);
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(html.result.toArray(new Text[0]));
    }

    private void parse(final Node start, HashSet<Element> elements, ArrayList<Text> result,
                       List<String> defaultClasses, String defaultFontFamily, String defaultColor,
                       double defaultFontSize/*, Background background*/) {
        for (int i = 0; i < start.childNodeSize(); i++) {
            final Node node = start.childNode(i);

            String name = node.nodeName();
            //System.out.println("name: " + name);

            if (!(name.equals("#root") || name.equals("html") || name.equals("head"))) {
                if (node.nodeName().startsWith("#")) {
                    TextNode tn = (TextNode) node;
                    //System.out.println("#text: '" + tn.text() + "'");

                    Text text = new Text(tn.text());

                    HashMap<String, String> styles = new HashMap<String, String>() {{
                        put("-fx-font-family", "\"" + defaultFontFamily + "\"");
                        put("-fx-fill", defaultColor);
                        put("-fx-font-size", String.valueOf(defaultFontSize));
                    }};

                    StringBuilder s = new StringBuilder();

                    for (Element e : elements) {
                        //appendTag(text, e.tagName().toLowerCase(), e);

                        for (int z = e.parents().size() - 1; z >= 0; z--) {
                            //for (int z = 0; z < e.parents().size(); z++) {
                            Element parent = e.parents().get(z);
                            //System.out.println("Element: " + e.tagName().toUpperCase() + " => parent: " + z + " - " + parent.tagName());

                            apply(text, parent, styles/*, background*/);
                        }

                        apply(text, e, styles/*, background*/);
                    }

                    for (Map.Entry<String, String> entry : styles.entrySet()) {
                        s.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
                    }

                    text.getStyleClass().addAll(defaultClasses);
                    text.setStyle(s.toString());

                    result.add(text);

                    elements.clear();
                } else {
                    Element element = (Element) node;
                    elements.add(element);
                }
            }

            if (node.childNodes().size() > 0)
                parse(node, elements, result, defaultClasses, defaultFontFamily, defaultColor, defaultFontSize/*, background*/);
        }
    }

    private void apply(Text text, Element element, HashMap<String, String> styles/*, Background background*/) {
        switch (element.tagName().toLowerCase()) {
            case "br":
                result.add(new Text("\n"));
                return;
            case "a":
                styles.put("-fx-fill", "#80CBC4");//#7DB2D8
                styles.put("-fx-cursor", "hand");
                styles.put("-fx-border-color", "#80CBC4");
                styles.put("-fx-border-width", "0 0 1 0");
                styles.put("-fx-underline", "true");
                text.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> hiperlink(text, isNowHovered));
                text.setOnMouseClicked(event -> {
                    try {
                        Desktop.getDesktop().browse(new URI(element.attr("href").replace("\\'", "")));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "b":
                styles.put("-fx-font-weight", "bold");
                break;
            case "font":
                styles.put("-fx-fill", element.attr("color"));
                break;
            case "small":
                styles.put("-fx-font-size", "10");
                break;
        }
        if (element.hasAttr("class")) {
            text.getStyleClass().addAll(element.attr("class").trim().replaceAll("( +)", "").split(" "));
        }
        if (element.hasAttr("style")) {
            for (String s : element.attr("style").split(";")) {
                String[] temp = s.split(":");
                String key = temp[0].trim(), value = temp[1].trim();
                switch (key) {
                    case "font-family":
                        styles.put("-fx-font-family", value);
                        break;
                    case "font-size":
                        styles.put("-fx-font-size", value);
                        break;
                    case "background-color":
                        //background.color(value);
                        break;
                    case "background-radius":
                        //background.radius(value);
                        break;
                }
            }
        }
    }

    /*private static final class Background {
        final ArrayList<String> colors = new ArrayList<>(), insets = new ArrayList<>(), radiuses = new ArrayList<>();
        final TextFlow textFlow;
        //final Scene scene;
        final Bounds textFlowPos;
        Background(@NotNull TextFlow textFlow) {
            this.textFlow = textFlow;
            //scene = textFlow.getScene();
            textFlowPos = textFlow.getBoundsInLocal();
        }
        final void color(String value) {
            colors.add(value);
        }
        final void radius(String value) {
            radiuses.add(value);
        }
        final void apply(@NotNull ArrayList<Text> list) {
            for (Text text : list) {
                Bounds pos = text.getBoundsInParent();
                double
                top = pos.getMinY(),
                //right = Math.min(scene.getWidth(), textFlowPos.getMaxX()) - pos.getMaxX(),
                //bottom = Math.min(scene.getHeight(), textFlowPos.getMaxY()) - pos.getMaxY(),
                right = textFlowPos.getMaxX() - pos.getMaxX(),
                bottom = textFlowPos.getMaxY() - pos.getMaxY(),
                left = pos.getMinX();
                insets.add(top + " " + right + " " + bottom + " " + left);
            }
            String _colors = build(colors), _insets = build(insets), _radiuses = build(radiuses);
            textFlow.setStyle(
                    (_colors == null ? "" : "-fx-background-color:" + _colors + ";") +
                    (_insets == null ? "" : "-fx-background-insets:" + _insets + ";") +
                    (_radiuses == null ? "" : "-fx-background-radius:" + _radiuses + ";"));
        }
        @Nullable String build(@NotNull ArrayList<String> values) {
            StringBuilder sb = new StringBuilder();
            for (String color : values) sb.append(color).append(",");
            String temp = sb.toString();
            return temp.equals("") ? null : temp.substring(0, temp.length() - 1);
        }
    }*/

    private void hiperlink(Text text, boolean hover) {
        String color = hover ? "#9cece5" : "#80CBC4";
        String[] current_styles = text.getStyle().split(";");
        for (int i = 0; i < current_styles.length; i++) {
            String[] temp = current_styles[i].split(":");
            String key = temp[0].trim(), value = temp[1].trim();
            if (key.equals("-fx-fill")) {
                current_styles[i] = "-fx-fill: " + color;
            }
            if (key.equals("-fx-border-color")) {
                current_styles[i] = "-fx-border-color: " + color;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : current_styles) {
            sb.append(s).append(";");
        }
        text.setStyle(sb.toString());
    }

}
