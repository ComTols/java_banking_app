package UI;

import Data.Person;

/**
 * Receives selected Persons from the select person pop up
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public interface ISelectReceiver {
    /**
     * Receives selected Persons from the select person pop up
     * @param contacts selected persons
     */
    public void receiveSelectedContacts(Person[] contacts);
}
