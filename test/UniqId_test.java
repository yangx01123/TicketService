import com.util.UniqId;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;

/**
 * Created by Yang on 24/01/2018.
 */
public class UniqId_test {

    @Test
    public void uniqValidation() {
        int numberRun = 50;
        for (int i = 0; i < numberRun; i++) {
            assertNotSame("Validate uniqueness of long",
                    UniqId.getInstance().uniqueCurrentTimeMS(), UniqId.getInstance().uniqueCurrentTimeMS());
            assertNotSame("Validate uniqueness of int",
                    UniqId.getInstance().uniqueInt(), UniqId.getInstance().uniqueInt());
        }
    }
}
