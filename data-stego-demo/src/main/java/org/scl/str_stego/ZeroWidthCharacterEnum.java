package org.scl.str_stego;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: sichaolong
 * @Date: 2023/8/11 13:50
 * @Description: 零宽字符与数值的映射类
 */
public enum ZeroWidthCharacterEnum {



    ZERO_WIDTH_CHARACTER_SPACE("space", "零宽间隔器", '\uFE0A', 65034, '0'),
    ZERO_WIDTH_CHARACTER_JOINER("joiner", "零宽连字标志", '\uFE0B', 65035, '1'),
    ZERO_WIDTH_CHARACTER_NONJOINER("non joiner", "零宽非连接符", '\uFE0C', 65036, '2'),
    ZERO_WIDTH_CHARACTER_LEFTTORIGHTMARK("left to right mark", "强制左至右控制码", '\uFE0D', 65037, '3'),
    ZERO_WIDTH_CHARACTER_VARIATIONSELECTOR15("variation selector 15", "左至右记号", '\uFE0E', 65038, '4'),
    ZERO_WIDTH_CHARACTER_VARIATIONSELECTOR16("variation selector 16", "右至左记号", '\uFE0F', 65039, '5'),
    ZERO_WIDTH_CHARACTER_TAGVERTICALBAR("tag vertical bar", "标志符号条目分组", '\uFE06', 65030, '6'),
    ZERO_WIDTH_CHARACTER_TAGSTARTOREND("tag start or end", "结束符号条目分组", '\uFE07', 65031, '7'),
    ZERO_WIDTH_CHARACTER_TAGOPEN("tag open", "开始符号条目分组", '\uFE08', 65032, '8'),
    ZERO_WIDTH_CHARACTER_TAGCLOSE("tag close", "字母加占位格式元素分组", '\uFE09', 65033, '9');


    private String name;
    private String chineseName;
    private Character character;
    private Integer codePointValue;
    private Character secretNumCharacter;

    private static final List<ZeroWidthCharacterEnum> inierList = new ArrayList<>();


    ZeroWidthCharacterEnum(String name, String chineseName, Character character, Integer codePointValue, Character secretNumCharacter) {
        this.name = name;
        this.chineseName = chineseName;
        this.character = character;
        this.codePointValue = codePointValue;
        this.secretNumCharacter = secretNumCharacter;
    }

    static {
        inierList.addAll(Arrays.asList(
                ZERO_WIDTH_CHARACTER_TAGOPEN,
                ZERO_WIDTH_CHARACTER_TAGCLOSE,
                ZERO_WIDTH_CHARACTER_TAGSTARTOREND,
                ZERO_WIDTH_CHARACTER_SPACE,
                ZERO_WIDTH_CHARACTER_JOINER,
                ZERO_WIDTH_CHARACTER_LEFTTORIGHTMARK,
                ZERO_WIDTH_CHARACTER_NONJOINER,
                ZERO_WIDTH_CHARACTER_TAGVERTICALBAR,
                ZERO_WIDTH_CHARACTER_VARIATIONSELECTOR15,
                ZERO_WIDTH_CHARACTER_VARIATIONSELECTOR16
        ));
    }

    /**
     * 加密
     * 根据数值型的 0-9 字符获取对应的0宽字符
     *
     * @param secretNumCharacter
     * @return
     * @throws Exception
     */

    public static Character getZwcBySecretNumCharacter(Character secretNumCharacter) throws Exception {
        if (Objects.isNull(secretNumCharacter)) {
            throw new Exception("param must be not null");
        }
        Character targetCharacter = inierList.stream().filter(i -> i.secretNumCharacter == secretNumCharacter).findFirst().map(i -> i.character).orElse(null);
        if (Objects.isNull(targetCharacter)) {
            throw new Exception("param must be number character!");
        }
        return targetCharacter;
    }

    /**
     * 解密
     * 根据0宽字符获取对应隐写的数值型的 0-9 字符组合
     *
     * @param zwc
     * @return
     * @throws Exception
     */

    public static Character getSecretNumCharacterByZwc(Character zwc) throws Exception {
        if (Objects.isNull(zwc)) {
            throw new Exception("param must not be null");
        }
        return inierList.stream().filter(k -> k.character.equals(zwc)).findFirst().map(k -> k.secretNumCharacter).orElse(null);
    }


}
