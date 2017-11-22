package in.joye.urlconnection.mime;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件类型的实体域
 * <p>
 * Created by joye on 2017/9/17.
 */

public class TypedFile implements TypedOutput, TypedInput {
    private static final int BUFFER_SIZE = 4096;

    private final String mimeType;
    private final File file;

    public TypedFile(@NonNull File file) {
        this("file", file);
    }

    public TypedFile(@NonNull String mimeType, @NonNull File file) {
        this.mimeType = mimeType;
        this.file = file;
    }

    public File file() {
        return file;
    }

    @Override
    public String fileName() {
        return file.getName();
    }

    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public long length() {
        return file.length();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream inputStream = new FileInputStream(file);
        try {
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        } finally {
            inputStream.close();
        }
    }

    @Override
    public InputStream in() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TypedFile) {
            TypedFile rhs = (TypedFile) o;
            return file.equals(rhs.file);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return "TypedFile{" +
                "mimeType='" + mimeType + '\'' +
                ", file=" + file.getAbsolutePath() +
                '}';
    }
}
