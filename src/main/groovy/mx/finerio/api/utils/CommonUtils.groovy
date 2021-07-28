package mx.finerio.api.utils


import java.security.SecureRandom

class CommonUtils {

    public static String createRandomString(Integer length) throws Exception {

        def secureRandom = new SecureRandom()
        def randomString = '' << ''

        for (int i = 0; i < length; i++) {

            def randomChar = null
            def charType = secureRandom.nextInt(3)

            if (charType == 0) {
                randomChar = 48 + secureRandom.nextInt(10)
            } else if (charType == 1) {
                randomChar = 65 + secureRandom.nextInt(26)
            } else {
                randomChar = 97 + secureRandom.nextInt(26)
            }

            randomString << (randomChar as char).toString()

        }
        return randomString;
    }

}
