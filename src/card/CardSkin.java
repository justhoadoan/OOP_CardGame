package card;

import java.util.HashMap;
import java.util.Map;

public class CardSkin {
    private String skinName;

    public CardSkin(String skinName) {
        this.skinName = skinName;
    }

    public String getSkinName() {
        return skinName;
    }

    public String getImagePath(String rank, String suit) {
            return "resources/cards/" + skinName + "/" + rank + suit + ".png";
    }
}