/**
 * $RCSfile$
 * version $Revision$
 * created 03.07.2007 10:07:20 by kunina
 * last modified $Date$ by $Author$
 * (C) ООО Крипто-Про 2004-2007.
 * <p/>
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован
 * при условии сохранения абзацев с указанием авторства и прав.
 * <p/>
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package com.example.testlake.TLS;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;

/**
 * Пример односторонней аутентификации TLS клиента.
 * В примере используется класс HttpsURLConnection.
 *
 * @author Copyright 2004-2007 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class ClientSample {

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String trustStorePath = "C:/trust.store";
        String trustStorePassword = "1";
        String urlPath = "https://www.cryptopro.ru/certsrv/certcarc.asp";

        System.setProperty("com.sun.security.enableCRLDP", "true");
        System.setProperty("com.ibm.security.enableCRLDP", "true");
        //SSLSocketFactory factory = TLSContext.initClientSSL(null, trustStorePath, trustStorePassword, null);


        //connect(factory, urlPath);

    }

    /**
     * Функция устанавливает подключение по заданному адресу
     * на основе переданного SSLSocketFactory.
     *
     * @param factory Объект SSLSocketFactory.
     * @param urlPath Адрес для подключения.
     *
     * @throws Exception
     */
    public static void connect(SSLSocketFactory factory,
        String urlPath) throws Exception {

        URL url = new URL(urlPath);

        // Установка нового соединения с заданным адресом.
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // Задание для него требуемого SSLSocketFactory.
        connection.setSSLSocketFactory(factory);

        // Вывод на экран содержимого запрошенной страницы.
        printContent(connection);

        // Разрыв соединения.
        connection.disconnect();

    }

    /**
     * Функция выводит на экран содержимое запрошенной страницы.
     *
     * @param connection Соединение.
     * @throws Exception
     */
    private static void printContent(HttpsURLConnection connection)
        throws Exception {

        if (connection != null) {

            BufferedReader br = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "windows-1251"));

            String input;
            while ((input = br.readLine()) != null) {
                System.out.println(input);
            }

            br.close();
        }

    }

}
