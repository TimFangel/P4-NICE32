package backend;

public final class InstructionFormatter {
    private InstructionFormatter() {}

    public static String format(String str, String comment) {
        String[] commentArr = comment.split("\n");
        return format(str, commentArr);
    }

    public static String format(String str, String[] comments) {
        StringBuilder result = new StringBuilder();

        String[] lines = str.split("\n");

        // Write each line based on arg count
        int i = 0;
        for (; i < lines.length; i++) {
            if (comments.length > i && !comments[i].isBlank()) {
                result.append(formatString(lines[i], comments[i]));
            } else {
                result.append(formatString(lines[i], ""));
            }
        }

        // Add extra comments
        for (; i < comments.length; i++) {
            result.deleteCharAt(result.length()-1);
            result.append("; ").append(comments[i].strip()).append("\n");
        }

        return result.toString();
    }

    private static String formatString(String line, String comment) {
        final int insLen = 9;
        final int argLen = 4;

        // format: ins arg, arg, arg ; com
        String format = "%-" + insLen + "s %" + argLen + "s %" + argLen + "s %" + argLen + "s %s";

        String com = "";
        StringBuilder newLine = new StringBuilder();

        String[] tokens = line.split(" ");

        // Get comment if exists
        if (!comment.isBlank()) {
            com = "; " + comment;
        }

        // Format based on amount of arguments
        switch (tokens.length) {
            case 2:
                newLine.append(String.format(format, tokens[0], tokens[1] + " ", "", "", com));
                break;
            case 3:
                newLine.append(String.format(format, tokens[0], tokens[1], tokens[2] + " ", "", com));
                break;
            case 4:
                newLine.append(String.format(format, tokens[0], tokens[1], tokens[2], tokens[3], com));
                break;

            default:
                newLine.append(String.format(format, tokens[0] + " ", "", "", "", com));
                break;
        }

        // Remove excess whitespaces
        int commentStartIndex = insLen + argLen * 3 + 4;
        while (newLine.length() > commentStartIndex && newLine.charAt(commentStartIndex) == ' ') {
            newLine.deleteCharAt(commentStartIndex);
        }

        return newLine.toString() + "\n";
    }
}