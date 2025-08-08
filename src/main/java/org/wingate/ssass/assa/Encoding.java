package org.wingate.ssass.assa;

public enum Encoding {
    ANSI(0, "ANSI"),
    Default(1, "Default"),
    Symbol(2, "Symbol"),
    ShiftJIS(128, "Shift-JIS"),
    Hangeul(129, "Hangeul"),
    Johab(130, "Johab"),
    SChinese(134, "GB2312"),
    TChinese(136, "BIG5"),
    Turkish(162, "Turkish"),
    Vietnamese(163, "Vietnamese"),
    Hebrew(177, "Hebrew"),
    Arabic(178, "Arabic");

    final int codepage;
    final String name;

    Encoding(int codepage, String name){
        this.codepage = codepage;
        this.name = name;
    }

    public int getCodepage() {
        return codepage;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", getCodepage(), getName());
    }
}
