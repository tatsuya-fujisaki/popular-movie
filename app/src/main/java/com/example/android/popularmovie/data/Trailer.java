package com.example.android.popularmovie.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Movie.class,
        parentColumns = "id",
        childColumns = "movie_id",
        onDelete = CASCADE,
        onUpdate = CASCADE),
        indices = {@Index("movie_id")})
public final class Trailer implements Parcelable {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String key;

    @SerializedName("iso_639_1")
    @ColumnInfo(name = "iso_639_1")
    public String iso639;

    @SerializedName("iso_3166_1")
    @ColumnInfo(name = "iso_3166_1")
    public String iso3166;

    public int size;

    @ColumnInfo(name = "movie_id")
    public int movieId;

    Trailer() {
    }

    private Trailer(Parcel parcel) {
        id = Objects.requireNonNull(parcel.readString());
        name = parcel.readString();
        key = parcel.readString();
        iso639 = parcel.readString();
        iso3166 = parcel.readString();
        size = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(key);
        dest.writeString(iso639);
        dest.writeString(iso3166);
        dest.writeInt(size);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}