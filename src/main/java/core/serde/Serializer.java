package core.serde;

import rx.Observable;

public interface Serializer {

    <T> Observable<String> serialize(Observable<T> object);

}
