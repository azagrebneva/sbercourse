package main;

import loader.Loader;
import loader.LoaderImpl;
import proxy.CachedAdvancedProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Задание 14. java.util.concurrent
 * Модифицировать кэш, из предыдущих домашних заданий по рефлексии и сериализации (описание ниже),
 * сделать его конкурентным. Должна быть возможность не лочить весь кэш,
 * а только тот элемент который в данный момент добавляется или удаляется.
 * Создание кеширующего прокси. Необходимо реализовать возможность тонкой настройки кеша:
 * 1. Указывать с помощью аннотаций, какие методы кешировать и как: просчитанный результат хранить в памяти JVM или сериализовывать в файле на диск.
 * 2. Возможность указывать, какие аргументы метода учитывать при определении уникальности результата,
 *    а какие игнорировать(по умолчанию все аргументы учитываются).
 *    Например, должна быть возможность указать, что doHardWork() должен игнорировать значение второго аргумента,
 *    уникальность определяется только по String аргументу.
 *     double r1 = service.doHardWork("work1", 10); //считает результат
 *     double r2 = service.doHardWork("work1", 5);  // результат из кеша, несмотря на то что второй аргумент различается
 * 3. Если возвращаемый тип это List – возможность указывать максимальное количество элементов в нем. То есть, если нам возвращается List с size = 1млн, мы можем сказать что в кеше достаточно хранить 100т элементов.
 * 4. Возможность указывать название файла/ключа по которому будем храниться значение. Если не задано - использовать имя метода.
 * 5. Если мы сохраняем результат на диск, должна быть возможность указать, что данный файл надо дополнительно сжимать в zip архив.
 * 6. Любые полезные настройки на ваш вкус.
 * 7. Все настройки кеша должны быть optional и иметь дефолтные настройки.
 * 8. Все возможные исключения должны быть обработаны с понятным описание, что делать, чтобы избежать ошибок.
 *    (Например, если вы пытаетесь сохранить на диск результат метода, но данный результат не сериализуем,
 *    надо кинуть исключение с понятным описанием как это исправить.)
 * 9. Логика по кешированию должна навешиваться с помощью DynamicProxy. Должен быть класс CacheProxy с методом cache(),
 *    который принимает ссылку на сервис и возвращает кешированную версию этого сервиса.
 *    CacheProxy должен тоже принимать в конструкторе некоторые настройки, например рутовую папку в которой хранить файлы,
 *    дефолтные настройки кеша и тд.
 *      CacheProxy cacheProxy = new CacheProxy(...);
 *      Service service = cacheProxy.cache(new ServiceImpl());
 *      Loader loader = cacheProxy.cache(new LoaderImpl());
 */

public class ProxyMain {

    public static void main(String[] args) {

        CachedAdvancedProxy cacheProxy = new CachedAdvancedProxy(null);
        Loader cachingLoader = cacheProxy.cache(new LoaderImpl());

        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        List<Future<Map<String,Integer>>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String product = "product" + (i % 3);
            Future<Map<String,Integer>> result = threadPool.submit(
                    () -> cachingLoader.doSomething(product));
            results.add(result);
        }
        threadPool.shutdown();

        // печатаем результаты
        results.forEach(future -> {
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
//        Service cachingService = cacheProxy.cache(new ServiceImpl());
//        Loader cachingLoader = cacheProxy.cache(new LoaderImpl());
//
//        System.out.println("Рез.:" + cachingService.run("milk",50.1, new Date()));
//        System.out.println("Рез.:" + cachingService.run("grape",100, new Date()));
//        System.out.println("Рез.:" + cachingService.run("grape",100, new Date()));
//        System.out.println("Рез.:" + cachingService.work("porridge"));
//        System.out.println("Рез.:" + cachingService.work("porridge"));
//
//        System.out.println("Рез.:" + cachingLoader.doSomething("milk"));
//        System.out.println("Рез.:" + cachingLoader.doSomething("milk"));
    }
}
