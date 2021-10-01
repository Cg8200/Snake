import java.awt.*;

/**
 * Created by cg8200 on 9/30/2021.
 */
public class Button {
    String btntxt;
    int btnX;
    int btnY;
    int btnHeight;
    int btnWidth;
    boolean isVisible;

    public Button(String btntxt, int btnX, int btnY, int btnHeight) {
        this.btntxt = btntxt;
        this.btnX = btnX;
        this.btnY = btnY;
        this.btnHeight = btnHeight;
        this.btnWidth = btntxt.length()*btnHeight*67/100;
        isVisible = false;
    }
    public void Draw(Graphics g) {
        g.setColor(Color.green);
        g.drawRect(btnX, btnY - btnHeight, btnWidth, btnHeight);
        Font myFont = new Font ("TimesRoman", Font.PLAIN, btnHeight-4);
        g.setFont(myFont);
        g.drawString(btntxt, btnX + btnHeight/2,btnY  - btnHeight/5);

    }
    public boolean isClicked(int mouseX, int mouseY){
        if(Snake.isAlive == false) {
            if (mouseX <= btnX + btnWidth && mouseX >= btnX) {
                if (mouseY <= btnY + btnHeight && mouseY >= btnY) {
                    isVisible = false;
                    return true;
                }
            }
        }
        return false;
    }
}
