package pl.thinkdata.droptop.database.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record AddressNumber(String street, String houseNumber, String flatNumber) {

    public static AddressNumber of(String address) {
        return parseAddress(address);
    }

    private static AddressNumber parseAddress(String address) {
        Pattern pattern = Pattern.compile(
                "^(.+?)\\s+(\\d+[A-Za-z]?)(?:\\s*(?:/|m\\.|lok\\.)\\s*(\\d+[A-Za-z]?))?$"
        );

        Matcher matcher = pattern.matcher(address.trim());
        if (matcher.find()) {
            String street = matcher.group(1);
            String hNo = matcher.group(2);
            String flatNo = matcher.group(3);
            return new AddressNumber(street, hNo, flatNo);
        }

        return new AddressNumber("", "", "");
    }
}
