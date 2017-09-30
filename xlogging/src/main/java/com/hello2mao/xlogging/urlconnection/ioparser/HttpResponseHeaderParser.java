package com.hello2mao.xlogging.urlconnection.ioparser;


import android.util.Log;

import com.hello2mao.xlogging.Constant;
import com.hello2mao.xlogging.urlconnection.CharBuffer;

/**
 * Http Response Header parser
 *
 * e.g.
 * Server: Tengine
 * Content-Type: application/json
 * Date: Mon, 20 Mar 2017 14:24:06 GMT
 * Vary: Accept-Encoding
 * X_TT_LOGID: 20170320222406010004052030814124
 * X-TT-LOGID: 20170320222406010004052030814124
 * Vary: Accept-Encoding
 * Content-Encoding: gzip
 * Vary: Accept-Encoding
 * Via: cache6.l2em21-1[55,0], cache8.cn199[73,0]
 * Timing-Allow-Origin: *
 * EagleId: 78258cd014900198467887486e
 * Transfer-Encoding: chunked
 * Proxy-Connection: Keep-alive
 */
public class HttpResponseHeaderParser extends HttpHeaderParser {

    private int parsedStatusCode;

    public HttpResponseHeaderParser(AbstractParserState parser, int parsedStatusCode) {
        super(parser);
        this.parsedStatusCode = parsedStatusCode;
    }

    @Override
    public boolean parse(CharBuffer charBuffer) {
        return super.parse(charBuffer);
    }

    /**
     * 到达header尾部后用此parser解析
     *
     * @return parser
     */
    protected AbstractParserState nextParserAfterEndOfHeader() {
        AbstractParserState parser;
        Log.d(Constant.TAG, "HttextParserAfterEndOfHeader isChunkedTransferEnHeader");
        if (notAllowedToHaveMessageBody()) {
            Log.d(Constant.TAG, "nextParserAfterEndOfHeader notAllowedToHaveMessageBody");
            getHandler().finishedMessage(getCharactersInMessage());
            parser = getHandler().getInitialParsingState();
        } else if (isChunkedTransferEncoding()) {
            Log.d(Constant.TAG, "nextParserAfterEndOfHeader isChunkedTransferEncoding");
            parser = new HttpChunkSizeParser(this);
        } else if (isContentLengthSet()) {
            Log.d(Constant.TAG, "nextParserAfterEndOfHeader isContentLengthSet");
            if (getContentLength() > 0) {
                parser = new HttpBodyParser(this, getContentLength());
            } else {
                getHandler().finishedMessage(getCharactersInMessage());
                parser = getHandler().getInitialParsingState();
            }
        } else if (getHandler().getParsedRequestMethod().equals("CONNECT")) {
            Log.d(Constant.TAG, "nextParserAfterEndOfHeader getParsedRequestMethod");
            getHandler().finishedMessage(getCharactersInMessage());
            parser= getHandler().getInitialParsingState();
        } else {
            Log.d(Constant.TAG, "nextParserAfterEndOfHeader EOFBodyParser");
            parser = new HttpEOFBodyParser(this);
        }
        return parser;
    }

    private boolean notAllowedToHaveMessageBody() {
        return (getHandler().getParsedRequestMethod().equals("HEAD"))
                || ((this.parsedStatusCode >= 100) && (this.parsedStatusCode <= 199))
                || (this.parsedStatusCode == 204)
                || (this.parsedStatusCode == 304);
    }
}