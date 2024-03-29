package UI;

import java.util.Date;

/**
 * Receives the birthday from the formatted text-fields as a parsed date
 * @author justus siegert
 * @version v1.0_stable_alpha
 */
public interface IParsedBirthdayReceiver {
    /**
     * Receive the date after loose focus
     * @param d the parsed date
     */
    public void setParsedDate(Date d);

    /**
     * Get the last received date or an default date
     * @return parsed date
     */
    public Date getParsedDate();
}
