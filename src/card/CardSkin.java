package card;

public class CardSkin {
    private String skinName;

    public CardSkin(String skinName) {
        this.skinName = skinName;
    }

    public String getImagePath(String rank, String suit) {
        return "/cards/" + skinName + "/" + rank + suit + ".png";
    }
}