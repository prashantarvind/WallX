package com.bentenstudio.wallx.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.bentenstudio.wallx.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    Context mContext;
    public static final int SAVE_TEMPORARY = 0;
    public static final int SAVE_PERMANENT = 1;

    public FileUtils(Context context){
        this.mContext = context;
    }

    public byte[] getBytes(String path) throws IOException{
        Uri uri = Uri.parse("file://"+path);
        InputStream iStream = mContext.getContentResolver().openInputStream(uri);
        return getBytes(iStream);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String getFileName(String path){
        return path.substring(path.lastIndexOf('/') + 1, path.length());
    }

    public BitmapFactory.Options getImageOptions(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        //If you want, the MIME type will also be decoded (if possible)
        String type = options.outMimeType;

        return options;
    }

    public boolean saveBitmapToStorage(Bitmap bitmap, String fileName, int saveOption){
        File temporaryDir = getTemporaryDir();
        File permanentDir = getPermanentDir();
        File file;

        switch (saveOption){
            case SAVE_PERMANENT:
                file = new File(permanentDir,fileName);
                break;
            case SAVE_TEMPORARY:
                file = new File(temporaryDir,fileName);
                break;
            default:
                file = new File(temporaryDir,fileName);
                break;
        }

        if(file.exists()){
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            if(saveOption == SAVE_PERMANENT){
                scanFile(file);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void copyFile(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyFile(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public void saveTemporaryFile(String url){
        final String fileName = getFileName(url);
        Glide.with(mContext).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                saveBitmapToStorage(resource,fileName,SAVE_TEMPORARY);
            }
        });
    }
    private boolean savedPermanent = false;
    public boolean savePermanentFile(final String url) {
        final boolean savedFinal;
        File temp = getTemporaryFile(getFileName(url));
        if (temp != null){
            try {
                copyFile(temp,getPermanentFile(getFileName(url)));
                savedPermanent = true;
            } catch (IOException e) {
                //e.printStackTrace();
            }
        } else {
            Glide.with(mContext).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    savedPermanent = saveBitmapToStorage(resource, getFileName(url), SAVE_PERMANENT);
                }
            });
        }

        return savedPermanent;
    }

    public boolean isSavedToStorage(String fileName){
        File temporaryFile = getTemporaryFile(fileName);
        return temporaryFile.exists();
    }

    public void removeTemporaryFile(String fileName){
        File file = new File(getTemporaryDir(),fileName);
        if(file.exists()){
            file.delete();
        }
    }

    public Bitmap getTemporaryBitmap(String fileName){
        String path = getTemporaryFilePath(fileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }

    private File getTemporaryDir(){
        File temporaryDir = new File(Environment.getExternalStorageDirectory(),mContext.getString(R.string.app_name)+"/temp");
        if(!temporaryDir.exists()){
            temporaryDir.mkdirs();
        }
        return temporaryDir;
    }

    public String getTemporaryFilePath(String fileName){
        File file = new File(getTemporaryDir(),fileName);
        return file.getAbsolutePath();
    }

    public File getTemporaryFile(String fileName){
        return new File(getTemporaryDir(),fileName);
    }

    private File getPermanentDir(){
        File permanentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                mContext.getString(R.string.app_name));
        if (!permanentDir.exists()){
            permanentDir.mkdirs();
        }
        return permanentDir;
    }

    public String getPermanentFilePath(String fileName){
        File file = new File(getTemporaryDir(),fileName);
        return file.getAbsolutePath();
    }

    public File getPermanentFile(String fileName){
        return new File(getPermanentDir(),fileName);
    }

    public File[] getFilesInPermanent(){
        File perm = getPermanentDir();
        return perm.listFiles();
    }

    void scanFile(File file){
        Intent intent =
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        mContext.sendBroadcast(intent);
    }

    public boolean setAsWallpaper(Bitmap bitmap) {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(mContext);
            wm.setBitmap(bitmap);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] compressBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
