package airtable.VO;

public class CheckType {
    /*
    VO5 coding。亚裔小哥刚来几个月。写一个函数来判断一个table的某一行是何种类型,number,还是text,还是category之类的。
    需要先讨论一下如何定义各种类型的rule，然后分清优先级就可以实现了。
    然后加了新的type怎么处理，这里要注意的是不要试图在一个method里边做所有的检查，而是一次只对一种type判断true/false这样会容易scale的多。
    果然‍下一个followup就是如果我可以支持custom的mapping，
    根据特定的rule来判断column的类型，其实就是把每一种检查都wrap到一个interface或者class里就好了。
    最后让自己写一套test case和跑test的方法。感觉这轮大脑一直在线应该是个strong。
     */
    enum Type {
        NUMBER, TEXT, CATEGORY, UNKNOWN
    }
    public Type checkType(String value) {
        if (isNumber(value)) {
            return Type.NUMBER;
        } else if (isText(value)) {
            return Type.TEXT;
        } else if (isCategory(value)) {
            return Type.CATEGORY;
        } else {
            return Type.UNKNOWN;
        }
    }

    private boolean isCategory(String value) {
        return true;
    }

    private boolean isText(String value) {
        return true;
    }

    private boolean isNumber(String value) {
        return true;
    }

}
