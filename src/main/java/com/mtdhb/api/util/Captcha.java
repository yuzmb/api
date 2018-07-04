package com.mtdhb.api.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/03
 */
public class Captcha {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String CHARACTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static Font ARIAL;

    static {
        try (InputStream in = Captcha.class.getClassLoader().getResourceAsStream("arial.ttf")) {
            ARIAL = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (FontFormatException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 32;
    private static final int DEFAULT_MARGIN = 0;
    private static final int DEFAULT_PADDING = 24;
    private static final int DEFAULT_SIZE = 4;

    private Random random = new Random();

    private int width;
    private int height;

    /**
     * 上下边距
     */
    private int margin;
    /**
     * 左右边距
     */
    private int padding;

    private int size;

    private StringBuilder code;
    private BufferedImage image;

    public Captcha() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MARGIN, DEFAULT_PADDING, DEFAULT_SIZE);
    }

    /**
     * @param width
     *            验证码图片宽度
     * @param height
     *            验证码图片高度
     * @param margin
     *            验证码图片上下边距
     * @param padding
     *            验证码图片左右边距
     * @param size
     *            验证码字符数
     */
    public Captcha(int width, int height, int margin, int padding, int size) {
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.padding = padding;
        this.size = size;
        this.code = new StringBuilder();
        this.image = new BufferedImage(width, height + (margin << 1), BufferedImage.TYPE_INT_RGB);
        init();
    }

    public String getCode() {
        return code.toString();
    }

    public BufferedImage getImage() {
        return image;
    }

    private void init() {
        Graphics g = image.getGraphics();
        Color defaultColor = g.getColor();
        Font defaultFont = g.getFont();
        logger.debug("defaultFont={}", defaultFont);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        Map<AttributedCharacterIterator.Attribute, Object> attributes = new HashMap<AttributedCharacterIterator.Attribute, Object>();
        // 设置字体大小，暂时先以高度像素为字体像素大小，java字体大小单位为pt，pt与px转换公式：pt = px * 3/4
        attributes.put(TextAttribute.FONT, Font.SERIF);
        attributes.put(TextAttribute.SIZE, height * 3 >> 2);
        for (int i = 0; i < size; i++) {
            // 设置随机旋转，±π/12
            attributes.put(TextAttribute.TRANSFORM,
                    AffineTransform.getRotateInstance((random.nextInt(2) == 0 ? -1 : 1) * Math.PI / 12));
            g.setFont(ARIAL.deriveFont(attributes));
            logger.debug("currentFont={}", g.getFont());
            g.setColor(new Color(75, 131, 8));
            char c = CHARACTER.charAt(random.nextInt(CHARACTER.length()));
            code.append(c);
            // 获得当前字体的字体规格。
            FontMetrics fm = g.getFontMetrics();
            // 通过图片宽度根据字符数等分获得单个字符应在图片占有宽度，再通过字体规格获得实际字符宽度
            // 居中坐标x = (占有宽度 - 字符宽度) / 2
            // 实际坐标x = 左边距 + 居中坐标x + 占有宽度 * 字符偏移量
            int x = padding + ((width - (padding << 1)) / size - fm.charWidth(c) >> 1)
                    + (width - (padding << 1)) / size * i;
            // 通过字体规格获得实际字符宽度
            // 居中基线坐标y = (高度 - 字符高度) / 2 + leading + ascent
            // 实际基线坐标y = 上边距 + 居中基线坐标y
            int y = margin + (height - fm.getHeight() >> 1) + fm.getLeading() + fm.getAscent();
            g.drawString(String.valueOf(c), x, y);
        }
        g.setColor(defaultColor);
        g.setFont(defaultFont);
    }

}
