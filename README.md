# JavaFXHtmlText (JavaFX Html Text)
The first and so far the only way to get normal support for HTML in the text for JavaFX

Features
--------
* Auto-link
* Line-break
* Default Text style Options
* Text ```-fx-background-color``` in process...
* Word break in process...

Dependencies
--------
```java
dependencies {
    ...
    compile 'org.jsoup:jsoup:1.12.1'
    ...
}
```

**Prepare TextFlow for best effect**
```java
TextFlow textFlow = new TextFlow();
textFlow.setPrefHeight(0);
textFlow.setMaxHeight(TextFlow.USE_PREF_SIZE);
```

Usage
--------
```java
Html.set(myTextFlow, "Test <font color='red'>red text</font> with <a href='https://gihub.com'>a test link</a>")
```

Usage with Options
--------
```css
.my-text-shadow-class {
    -fx-effect: dropshadow(one-pass-box, black, 10,0,0,0);
}
```
```java
Html.set(myTextFlow, "Test <font color='red'>red text</font> with <a href='https://gihub.com'>a test link</a>",
    new Html.Options()
        .setDefaultFontFamily("Roboto Condensed Regular")
        .setDefaultColor("#cecece")
        .setDefaultFontSize(20)
        .setDefaultClasses("my-text-shadow-class class-1 class-2 etc"))
```

Any of your ideas are accepted!
--------
