package com.lschej.rewardsserver.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

import com.lschej.rewardsserver.data.Member;
import com.lschej.rewardsserver.database.DatabaseController;

public class MemberRepository {

    private static final Logger logger = Logger.getLogger(MemberRepository.class.getName());

    public void create(Member member) {
        String sql = """
            INSERT INTO Members
                (PhoneNumber, FirstName, LastName, Birthday, EmailAddress)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, member.getPhoneNumber());
            statement.setString(2, member.getFirstName());
            statement.setString(3, member.getLastName());
            statement.setObject(4, member.getBirthday());
            statement.setString(5, member.getEmailAddress());

            statement.executeUpdate();

            logger.info("Member row created.");
        } catch (SQLException e) {
            String message = "SQL Exception: " + e + " Member Create Dump: %s, %s, %s, %s, %s";
            logger.warning(message.formatted(member.getPhoneNumber(), member.getFirstName(), member.getLastName(), member.getBirthday(), member.getEmailAddress()));
        }

    }

    public Optional<Member> retrieve(String phoneNumber) {
        String sql = """
             SELECT PhoneNumber, FirstName, LastName, Birthday, EmailAddress
             FROM Members
             WHERE PhoneNumber = ?   
        """;
        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                Member member = new Member(
                        resultSet.getString("PhoneNumber"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getObject("Birthday", LocalDate.class),
                        resultSet.getString("EmailAddress")
                );

                logger.info("Member %s retrieved.".formatted(phoneNumber));

                return Optional.of(member);
            }
        } catch (SQLException e) {
            String message = "SQL Exception: " + e + " Retrieve Phone Number: " + phoneNumber;
            logger.warning(message);
            return Optional.empty();
        }
    }

    public void update(Member member) {
        String sql = """
            UPDATE Members
            SET FirstName = ?,
                LastName = ?,
                Birthday = ?,
                EmailAddress = ?
            WHERE PhoneNumber = ?
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setObject(3, member.getBirthday());
            statement.setString(4, member.getEmailAddress());
            statement.setString(5, member.getPhoneNumber());

            statement.executeUpdate();

            logger.info("Member %s updated.".formatted(member.getPhoneNumber()));

        } catch (SQLException e) {
            String message = "SQL Exception: " + e + " Member Update Dump: %s, %s, %s, %s, %s";
            logger.warning(message.formatted(member.getPhoneNumber(), member.getFirstName(), member.getLastName(), member.getBirthday(), member.getEmailAddress()));
        }
    }

    public void delete(String phoneNumber) {
        String sql = """
            DELETE FROM Members
            WHERE PhoneNumber = ?
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {

            statement.setString(1, phoneNumber);

            statement.executeUpdate();

            logger.info("Member %s deleted.".formatted(phoneNumber));

        } catch (Exception e) {
            String message = "SQL Exception: " + e + " Remove Phone Number: " + phoneNumber;
            logger.warning(message);
        }

    }
}
