# TTFFont
The TTF Font Library for JME3.
You can start using ttf fonts in the ui with JME3 and with Nifty!
Example:
```java
assetManager.registerLoader(TrueTypeLoader.class, "ttf");
TrueTypeFont font = (TrueTypeFont) assetManager.loadAsset("Interface/arial.ttf");
font.setColor(ColorRGBA.Blue);
font.setSize(40);
TrueTypeText text = font.render("hi");
text.setLocalTranslation(settings.getWidth() / 2 - text.getWidth() / 2, settings.getHeight() / 2 - text.getHeight() / 2, 0);
guiNode.attachChild(text);
```
Example with Nifty:
```java
assetManager.registerLoader(TrueTypeLoader.class, "ttf");
TTFSupportedNiftyJmeDisplay display = new TTFSupportedNiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
guiViewPort.addProcessor(display);
Nifty nifty = display.getNifty();
nifty.loadControlFile("nifty-default-controls.xml");
nifty.fromXml("Interface/Test.xml", "Test");
```
Interface/Test.xml
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<nifty xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://nifty-gui.lessvoid.com/nifty-gui" xsi:schemaLocation="https://raw.githubusercontent.com/void256/nifty-gui/1.4/nifty-core/src/main/resources/nifty.xsd https://raw.githubusercontent.com/void256/nifty-gui/1.4/nifty-core/src/main/resources/nifty.xsd">
    <screen id="Test">
        <layer id="GLayer0" childLayout="center">
            <panel childLayout="vertical">
                <control name="label" id="GLabel0" color="#0000ff" text="hello" font="Interface/arial.ttf501"/>
                <control name="label" id="GLabel0" color="#0000ff" text="مرحبا" font="Interface/arial.ttf501"/>
            </panel>
        </layer>
    </screen>
</nifty>
```
The passed font path in the previous example is Interface/arial.ttf. You should write the size after the path then write a one number represents the style of the font.
* 0 for plain text.
* 1 for bold.
* 2 for italic.
* 3 for bold italic.
## Notes
This library supports languages such as arabic.
Until now this library supports only PC because of it's internal use of AWT.

