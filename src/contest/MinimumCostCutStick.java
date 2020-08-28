package contest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MinimumCostCutStick {
    public static int minCost(int n, int[] cuts) {
        Map<String, Integer> memo = new HashMap<>();
        Arrays.sort(cuts);
        return findMin(memo, cuts, 0, n, 0, cuts.length - 1);
    }

    private static int findMin(Map<String, Integer> memo, int[] cuts, int leftPos, int rightPos, int leftIndex, int rightIndex) {
        String key = leftIndex + "," + rightIndex;
        if (memo.containsKey(key)) return memo.get(key);
        if (leftIndex > rightIndex) return 0;
        int len = rightPos - leftPos;
        if (leftIndex == rightIndex) return len;
        int min = Integer.MAX_VALUE;

        for (int i = leftIndex; i <= rightIndex; i++) {
            int val = len
                    + findMin(memo, cuts, leftPos, cuts[i], leftIndex, i - 1)
                    + findMin(memo, cuts, cuts[i], rightPos,i + 1, rightIndex);
            min = Math.min(min, val);
        }
        memo.put(key, min);
        return min;
    }

    public static void main(String[] args) {
        final String ALL_FIELDS = "id, thread_id, created_at, network_id, sender_id, message_type, deleted_at, " +
                "updated_at, replied_to_id, state, shared_message_id, sender_type, reply_count, client_type_id, " +
                "title, has_cross_group_attachments, chat_client_sequence, message_subtype, text(messages.\"references\") AS references, text(additional_data) AS additional_data, " +
                "body, language, version_num, deleted_by_id, text(content_state) AS content_state, delegate_id";
        final String SELECT_THREAD_STARTER_PATTERN = String.format("(SELECT %s FROM messages WHERE id = :%%s)", ALL_FIELDS);
        System.out.println(SELECT_THREAD_STARTER_PATTERN);
    }
}
