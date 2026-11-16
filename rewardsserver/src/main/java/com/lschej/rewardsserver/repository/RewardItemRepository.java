package com.lschej.rewardsserver.repository;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import com.lschej.rewardsserver.data.RewardItem;
import com.lschej.rewardsserver.database.DatabaseController;

public class RewardItemRepository {

    private static final Logger logger = Logger.getLogger(RewardItemRepository.class.getName());

    public void create(RewardItem reward) {
        String sql = """
            INSERT INTO RewardItems
                (RewardName, RewardImage, RewardPrice)
            VALUES (?, ?, ?)
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, reward.getName());
            statement.setBytes(2, imageToBytes(reward.getImage()));
            statement.setInt(3, reward.getPrice());

            statement.executeUpdate();

            String message = "Reward item added.";
            logger.info(message);
        } catch (SQLException e) {
            String message = "SQL Exception " + e + " Reward Item not added";
            logger.warning(message);
        }
    }

    public List<RewardItem> retrieveAll() {
        String sql = """
            SELECT *
            FROM RewardItems
        """;

        List<RewardItem> rewards = new ArrayList<>();

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            try (ResultSet results = statement.executeQuery();) {
                while (results.next()) {
                    RewardItem reward = new RewardItem(
                            results.getInt("RewardID"),
                            imageFromBytes(results.getBytes("RewardImage")),
                            results.getString("RewardName"),
                            results.getInt("RewardPrice")
                    );

                    rewards.add(reward);
                }
            }

            String message = "Reward Item rows retrieved.";
            logger.info(message);
            return rewards;
        } catch (SQLException e) {
            String message = "SQL Exception " + e + " Reward Item rows failed to retrieve";
            logger.warning(message);
            return rewards;
        }
    }

    public Optional<RewardItem> retrieve(int rewardId) {
        String sql = """
            Select * 
            FROM RewardItems
            WHERE RewardID = ?
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, rewardId);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    String message = "Reward Item row retrieved.";
                    logger.info(message);

                    return Optional.of(new RewardItem(
                            results.getInt("RewardID"),
                            imageFromBytes(results.getBytes("RewardImage")),
                            results.getString("RewardName"),
                            results.getInt("RewardPrice")
                    ));
                }
            }
        } catch (SQLException e) {
            String message = "SQL Exception " + e + " Reward Item failed to retrieve: " + rewardId;
            logger.warning(message);
        }
        return Optional.empty();
    }

    public void update(RewardItem reward) {
        String sql = """
            UPDATE RewardItems
            SET RewardName = ?,
                RewardImage = ?,
                RewardPrice = ?
            WHERE RewardID = ?
        """;

        try (Connection connection = DatabaseController.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reward.getName());
            statement.setBytes(2, imageToBytes(reward.getImage()));
            statement.setInt(3, reward.getPrice());
            statement.setInt(4, reward.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                String message = "Reward item not found for update: " + reward.getId();
                logger.warning(message);
                return;
            }

            String message = "Reward item updated: " + reward.getId();
            logger.info(message);
        } catch (SQLException e) {
            String message = "SQL Exception " + e + " Reward Item failed to update: " + reward.getId();
            logger.warning(message);
        }
    }

    public void delete(int rewardId) {
        String sql = """
            DELETE FROM RewardItems
            WHERE RewardID = ?
        """;

        try (Connection connection = DatabaseController.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, rewardId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                String message = "Reward item not found for deletion: " + rewardId;
                logger.warning(message);
                return;
            }

            String message = "Reward item deleted: " + rewardId;
            logger.info(message);
        } catch (SQLException e) {
            String message = "SQL Exception " + e + " Reward Item failed to delete: " + rewardId;
            logger.warning(message);
        }
    }

    private static byte[] imageToBytes(ImageIcon imageIcon) throws SQLException {
        if (imageIcon == null || imageIcon.getIconWidth() <= 0 || imageIcon.getIconHeight() <= 0) {
            return null;
        }

        BufferedImage image = new BufferedImage(
                imageIcon.getIconWidth(),
                imageIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        image.getGraphics().drawImage(imageIcon.getImage(), 0, 0, null);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new SQLException("Could not encode reward image", e);
        }
    }

    private static ImageIcon imageFromBytes(byte[] imageBytes) {
        return imageBytes == null ? null : new ImageIcon(imageBytes);
    }
}
