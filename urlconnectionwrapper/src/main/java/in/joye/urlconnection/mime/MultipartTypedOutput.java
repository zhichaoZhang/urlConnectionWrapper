package in.joye.urlconnection.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * 富类型请求体
 * 基于post请求，针对请求头和请求体做额外约定：
 * <p>
 * 1. Content-Type: multipart/form-data; boundary=${bound}
 * 2. 规定一个内容分割符（boundary）用于分割请求体中的多个post的内容
 * <p>
 * Created by joye on 2017/9/17.
 */

public class MultipartTypedOutput implements TypedOutput {
    public static final String DEFAULT_TRANSFER_ENCODING = "binary";

    /*
        每块mime
     */
    private static final class MimePart {
        private final TypedOutput body;
        private final String name;
        private final String transferEncoding;
        private final boolean isFirst;
        private final String boundary;

        private byte[] partBoundary;
        private byte[] partHeader;
        private boolean isBuild;

        public MimePart(String name, String transferEncoding, TypedOutput body, boolean isFirst, String boundary) {
            this.body = body;
            this.name = name;
            this.transferEncoding = transferEncoding;
            this.isFirst = isFirst;
            this.boundary = boundary;
        }

        /**
         * 将当前块内容写入到输出流中
         * 1、写分隔符
         * 2、写描述信息
         * 3、写内容
         *
         * @param outputStream Http输出流
         * @throws IOException 写流异常
         */
        public void writeTo(OutputStream outputStream) throws IOException {
            build();
            outputStream.write(partBoundary);
            outputStream.write(partHeader);
            body.writeTo(outputStream);
        }

        /**
         * 获取当前块长度
         *
         * @return 内容长度 + 分隔符长度 + 描述头长度
         */
        public long size() {
            build();
            if (body.length() > -1) {
                return body.length() + partBoundary.length + partHeader.length;
            } else {
                return -1;
            }
        }

        private void build() {
            if (isBuild) return;
            partBoundary = buildBoundary(boundary, isFirst, false);
            partHeader = buildHeader(name, transferEncoding, body);
            isBuild = true;
        }
    }

    /**
     * 带顺序的块列表
     */
    private final List<MimePart> mimeParts = new LinkedList<>();

    /**
     * 随机分割字符串
     */
    private final String mBoundary;

    /**
     * 请求体结束字符
     */
    private final byte[] mFooter;

    /**
     * 请求体长度
     */
    private long mLength;

    public MultipartTypedOutput() {
        this(UUID.randomUUID().toString());
    }

    public MultipartTypedOutput(String boundary) {
        this.mBoundary = boundary;
        mFooter = buildBoundary(boundary, false, true);
        mLength = mFooter.length;
    }

    @Override
    public String fileName() {
        return null;
    }

    @Override
    public String mimeType() {
        return "multipart/form-data; boundary=" + mBoundary;
    }

    @Override
    public long length() {
        return mLength;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        for(MimePart part : mimeParts) {
            part.writeTo(outputStream);
        }
        outputStream.write(mFooter);
    }

    /*
        得到每块内容的字节数组表示
     */
    List<byte[]> getParts() throws IOException {
        List<byte[]> parts = new ArrayList<>(mimeParts.size());
        for (MimePart mimePart : mimeParts) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mimePart.writeTo(bos);
            parts.add(bos.toByteArray());
        }
        return parts;
    }

    public void addPart(String name, TypedOutput body) {
        addPart(name, DEFAULT_TRANSFER_ENCODING, body);
    }

    public void addPart(String name, String transferEncoding, TypedOutput body) {
        if (name == null) {
            throw new NullPointerException("part name must not be null.");
        }
        if (transferEncoding == null) {
            throw new NullPointerException("Transfer encoding must not be null.");
        }
        if (body == null) {
            throw new NullPointerException("Part body must not be null.");
        }
        MimePart part = new MimePart(name, transferEncoding, body, mimeParts.isEmpty(), mBoundary);
        mimeParts.add(part);

        //计算请求体总长度
        long size = part.size();
        if (size != -1) {
            mLength = -1;
        } else if (mLength != -1) {
            mLength += size;
        }
    }

    public int getPartSize() {
        return mimeParts.size();
    }

    /*
        构建分隔符
        --${boundary}
        --${boundary}
        --${boundary}--
     */
    private static byte[] buildBoundary(String boundary, boolean first, boolean last) {
        try {
            //StringBuilder初始化大小为最后一个分隔符长度，包含四个-，一对回车换行，一个随机字符串boundary
            StringBuilder sb = new StringBuilder(boundary.length() + 8);
            if (!first) {
                sb.append("\r\n");
            }
            sb.append("--");
            sb.append(boundary);
            if (last) {
                sb.append("--");
            }
            sb.append("\r\n");

            return sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to write multipart boundary", e);
        }
    }

    /*
        构建每块内容描述信息
        相当于每块的请求头
     */
    private static byte[] buildHeader(String name, String transferEncoding, TypedOutput value) {
        try {
            //初始化大小基于固定头部和保守的值长度来估计
            StringBuilder headers = new StringBuilder(128);

            //内容描述，如果是文件，name值是服务端存储文件名称
            headers.append("Content-Disposition: form-data; name=\"");
            headers.append(name);

            String fileName = value.fileName();
            if (fileName != null) {
                //客户端文件名
                headers.append("\"; filename=\"");
                headers.append(fileName);
            }

            //指定内容类型
            headers.append("\"\r\nContent-Type: ");
            headers.append(value.mimeType());

            long length = value.length();
            if (length != -1) {
                //添加请求长度
                headers.append("\r\nContent-Length: ").append(length);
            }

            //添加内容传输编码方式
            headers.append("\r\nContent-Transfer-Encoding: ");
            headers.append(transferEncoding);
            headers.append("\r\n");


            headers.append("\r\n");


            return headers.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to write multipart header", e);
        }
    }
}
