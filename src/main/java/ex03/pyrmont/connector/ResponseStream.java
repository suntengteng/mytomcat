package ex03.pyrmont.connector;

import ex03.pyrmont.connector.http.HttpResponse;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Convenience implementation of <b>ServeletOutputStream</b> that works with the standard ResponseBase of <b>Response</b>
 * if the content length has been set on our associated Response ,this implementation will enforce not writing more than that many
 * bytes on the underlying stream
 * Created by ST on 2016/12/15.
 */
public class ResponseStream extends ServletOutputStream {

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a servlet output stream associated with the specified Request
     * @param response
     */
    public ResponseStream(HttpResponse response){
        super();
        closed = false;
        commit = false;
        count = 0;
        this.response = response;
    }

    //-------------------------------------------------Instance Variables

    /**
     * Has this stream been closed?
     */
    protected boolean closed = false;

    /**
     * Should we commit the response when we are flushed?
     */
    protected boolean commit = false;

    /**
     * The number length past which we will not write,or -1 if there is no defined content length
     */
    protected int count = 0;

    /**
     * The content length past which we will not write ,or -1 if there is
     * no defined content length
     */
    protected int length = -1;


    /**
     * The Response with which this input stream is associated.
     */
    protected HttpResponse response = null;

    /**
     * The underlying output stream to which we should write data.
     */
    protected  OutputStream stream = null;

    //-----------------------------------------------------Properties


    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }

    /**
     * Close this output stream ,causing any buffered data to be flushed and
     * any further output data to throw an IOException
     * @throws IOException
     */
    public void close()throws IOException{
        if(closed)
            throw new IOException("responseStream.close.closed");
        response.flushBuffer();
        closed = true;
    }

    /**
     * Flush any buffered data for this output stream ,which also causes the response to be committed.
     *
     * @throws IOException
     */
    public void flush() throws IOException{
        if(closed)
            throw new IOException("responseStream.flush.closed");
           if(commit)
               response.flushBuffer();
    }



    @Override
    public void write(int b) throws IOException {
        if(closed)
            throw new IOException("responseStream.write.closed");
        if((length > 0) && count >= length)
            throw new IOException("responseStream.write.count");
        response.write(b);
        count++;
    }

    public void write(byte b[]) throws IOException{
        write(b,0,b.length);
    }

    public void write(byte b[],int off,int len) throws IOException{
        if(closed)
            throw new IOException("responseStream.write.closed");
        int actual = len;
        if(length > 0 && (count + len) >= length){
            actual = length -count;
        }
        response.write(b,off,actual);
        count += actual;
        if(actual < len)
            throw new IOException("responseStream.write.count");
    }

    // -------------------------------------------------------- Package Methods

    /**
     * Has this response stream been closed?
     */
    boolean closed() {
        return (this.closed);

    }


    /**
     * Reset the count of bytes written to this stream to zero.
     */
    void reset() {

        count = 0;

    }

}
