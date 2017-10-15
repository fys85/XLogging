package com.hello2mao.xlogging.urlconnection.io.parser;


import com.hello2mao.xlogging.urlconnection.CharBuffer;

public class NewlineLineParser extends AbstractParser {
    private AbstractParser nextParserAfterNewline;

    public NewlineLineParser(AbstractParser parser)
    {
        super(parser);
        this.nextParserAfterNewline = parser;
    }

    @Override
    public boolean add(int data)
    {
        if (data == -1)
        {
            getHandler().setNextParserState(NoopLineParser.DEFAULT);
            return true;
        }
        this.charactersInMessage += 1;
        if ((char) data == '\n')
        {
            this.nextParserAfterNewline.setCharactersInMessage(getCharactersInMessage());
            getHandler().setNextParserState(this.nextParserAfterNewline);
            return true;
        }
        return false;
    }

    @Override
    protected int getInitialBufferSize()
    {
        return 0;
    }

    @Override
    protected int getMaxBufferSize()
    {
        return 0;
    }

    @Override
    public AbstractParser nextParserAfterBufferFull()
    {
//        Assert.is(false);
        return this;
    }

    @Override
    public AbstractParser nextParserAfterSuccessfulParse()
    {
//        Assert.is(false);
        return this;
    }

    @Override
    public boolean parse(CharBuffer paramCharBuffer)
    {
//        CustomLog.d(CustomLog.defaultTag, "NewlineLineParser parse");
//        Assert.is(false);
        return true;
    }
}