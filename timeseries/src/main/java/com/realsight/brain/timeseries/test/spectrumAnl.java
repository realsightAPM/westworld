package com.realsight.brain.timeseries.test;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Created by abedaigorou on 16/04/10.
 */
public class spectrumAnl extends SFFT
{
    private int length;
    private int width[];
    private int height[];
    private final int offset=2;
    private WritableImage writableImage;
    private PixelWriter pixelWriter;
//    private ImageView image=new ImageView(new Image("http://cdn-ak.f.st-hatena.com/images/fotolife/G/Grano/20151104/20151104015247.jpg"));
    private ImageView image;
    private int imageWidth;
    private int imageHeight;
    private Thread pThread;
    private boolean pisRunning=true;

    public spectrumAnl(int imageWidth, final int imageHeight)
    {
        this.length=N;
        width=new int[length];
        height=new int[length];
        writableImage=new WritableImage(imageWidth,imageHeight);
        this.imageWidth=imageWidth;
        this.imageHeight=imageHeight;
        pixelWriter=writableImage.getPixelWriter();
        image=new ImageView(writableImage);

        pThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (!pisRunning)
                        break;

                    for(int x=0;x<length;x++) {
                        for(int c=0;c<imageHeight;c++)
                            pixelWriter.setColor(x,c, Color.WHITE);
                        if(FFTData==null)
                            break;
                        for (int y = imageHeight-1; y >imageHeight-(FFTData[x].intValue()*50); y--) {
                            if (y > imageHeight-1|| y<0)
                                break;
                            pixelWriter.setColor(x, y, Color.RED);
                        }
                    }
                }

            }
        });
        pThread.start();
    }

    @Override
    public void Stop()
    {
        super.Stop();
        pisRunning=false;
    }

    public ImageView getImage()
    {
        return image;
    }
}
