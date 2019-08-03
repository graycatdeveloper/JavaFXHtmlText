# JavaFXHtmlText
The first and so far the only way to get normal support for HTML in the text for JavaFX

Features
--------
* Auto-link
* Line-break
* Default Text style Options
* Text ```-fx-background-color``` in process...

Dependencies
--------
```java
dependencies {
    ...
    compile 'org.jsoup:jsoup:1.12.1'
    ...
}
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
        .setDefaultClasses("my-text-shadow-class"))
```
