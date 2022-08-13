package databricks;

public class IPToCIDR {

    // https://www.1point3acres.com/bbs/thread-844718-1-1.html
    // https://www.1point3acres.com/bbs/thread-889799-1-1.html
    public static boolean ipMatchCidr(String ip, String cidr) {
        String[] split = cidr.split("/");
        int tail = split.length == 2 ? Integer.parseInt(split[1]) : 32;
        long cidrValue = ipToInt(split[0]);
        long ipValue = ipToInt(ip);

        // eg: for x.x.x.x:24 this will get 24 1 concat with 8 0
        // if tail = 32 this will overflow the number
        int mask = tail == 32 ? 0xffffffff : ((1 << tail) - 1) << (32 - tail);
        return (ipValue & mask) == (cidrValue & mask);
    }

    public static boolean ipMatchCidr2(String ip, String cidr) {
        String[] split = cidr.split("/");
        int tail = split.length == 2 ? Integer.parseInt(split[1]) : 32;
        long cidrValue = ipToInt(split[0]);
        long ipValue = ipToInt(ip);
        if (tail == 32) {
            return cidrValue == ipValue;
        }
        int len = 32 - tail;
        int mask = 1;
        while (len-- > 0) {
            mask <<= 1;
        }
        return (ipValue & mask) == (cidrValue & mask);
    }

    private static long ipToInt(String ip) {
        String[] split = ip.split("\\.");
        if (split.length != 4) return -1;
        int res = 0;
        for (String s : split) {
            res = (res << 8) + Integer.parseInt(s);
        }
        return res;
    }

    public static void main(String[] args) {
        String ip = "192.168.0.3", cidr = "192.168.0.4/30";
        String ip2 = "255.255.255.255", cidr2 = "255.255.255.252/30";
        System.out.println(ipMatchCidr(ip2, cidr2));
        System.out.println(ipMatchCidr2(ip2, cidr2));
    }
}
