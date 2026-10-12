package com.filmbox.repository;

import com.filmbox.model.Movie;
import com.filmbox.model.TvShow;

import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    public static List<Movie> getDefaultMovies() {
        List<Movie> list = new ArrayList<>();

        // Kenyan & African Masterpieces
        list.add(createMovie(130737, "Nairobi Half Life", "Mwas, a young aspiring actor from upcountry Kenya dreams of becoming an accomplished actor one day, and in pursuit of this, he makes his way to Nairobi, the city of opportunity...", "/1QYW51WrwmSsJBm1y9ZkdEeHp6q.jpg", "/xhEkKDeFCBxK7p3Lo7p1RRZbN4Z.jpg", "2012-07-21", 6.9));
        list.add(createMovie(1063242, "Disconnect: The Wedding Planner", "After falling victim to a scam, a desperate man races the clock as he attempts to plan a luxurious destination wedding for an important investor.", "/co6doQ2d3f5ZaTTvORV76wrTFit.jpg", "/e1cC9muSRtAHVtF5GJtKAfATYIT.jpg", "2023-01-13", 6.9));
        list.add(createMovie(493915, "Supa Modo", "Jo, a witty 9-year old terminally ill girl is taken back to her rural village to live out the rest of her short life...", "/gvRrPZQlYX1bwwTxxtuerrADviz.jpg", "/81QUyXRJuCh5U78DDuWKyb56BHa.jpg", "2018-02-18", 7.4));
        list.add(createMovie(47559, "The First Grader", "The true story of an 84 year-old Kenyan villager and ex Mau Mau freedom fighter who fights for his right to go to school...", "/jstfYxEYMFfSO6tQaYL6O0AMUol.jpg", "/tQRygoT0GWUvlZe0mcTAKf9RDbb.jpg", "2010-09-12", 7.1));
        list.add(createMovie(1129249, "Click Click Bang", "When Kev, a super talented footballer's dream come crushing down, he is left with no choice but to join the criminal world.", "/1TQ7FAqdtPk5VOCT1wgQvpEETlL.jpg", "/1TQ7FAqdtPk5VOCT1wgQvpEETlL.jpg", "2023-10-19", 8.0));
        list.add(createMovie(1206988, "A Tribe Called Judah", "With the help of their mother, the Judah boys plan to rob a small mall. But when they arrive, they're met by real armed robbers.", "/9aMG2ftIFqFAN69FdjovKJY0hsd.jpg", "/jX7Mb37fa7JhB9c3acL1evERNwW.jpg", "2023-12-15", 9.1));
        list.add(createMovie(1172009, "The Black Book", "After his son is wrongly accused of kidnapping, a deacon who has just lost his wife takes matters into his own hands.", "/kn28W24slBLyGr8ZIZnxNE5YZrY.jpg", "/9WxqnP9c29wXd03sALSpxpTx8fk.jpg", "2023-09-22", 6.7));
        list.add(createMovie(1023994, "Aníkúlápó", "After an affair with a queen leads to his demise, an eager traveler encounters a mystical bird with the power to give him another life.", "/xb30hkUpBm23stnVgDJGYGsC0R0.jpg", "/gCojEROJs4JUVCCMA4fDFGc8OFc.jpg", "2022-09-30", 7.1));
        list.add(createMovie(1163045, "Jagun Jagun", "A young man determined to become a powerful warrior joins an elite army, encountering the wrath of a maniacal warlord.", "/uPZtE5DSo9VIX2N3NPtmTayhgP8.jpg", "/4Yon9Qmg3U4onL0OywXAHSkFTUG.jpg", "2023-08-10", 6.7));
        list.add(createMovie(570465, "King of Boys", "The story of Alhaja Eniola Salami, a businesswoman and philanthropist with a checkered past and a promising political future.", "/3nO3VboeVLVOwIHMkTRIwTrrKQP.jpg", "/8lUYBdpIo8ysI9UzVn8Zs1AwB5x.jpg", "2018-10-26", 6.4));

        // Global Blockbusters
        list.add(createMovie(533535, "Deadpool & Wolverine", "A listless Wade Wilson toils away in civilian life. But when his homeworld faces an existential threat, Wade must reluctantly suit-up again with Wolverine.", "/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg", "/by8z9Fe8y7p4jo2YlW2SZDnptyT.jpg", "2024-07-24", 7.6));
        list.add(createMovie(558449, "Gladiator II", "Years after witnessing the death of the revered hero Maximus, Lucius is forced to enter the Colosseum after his home is conquered.", "/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg", "/tOqIwliWMovSIZ9DyvHcHI7p2im.jpg", "2024-11-13", 6.6));
        list.add(createMovie(693134, "Dune: Part Two", "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge.", "/6izwz7rsy95ARzTR3poZ8H6c5pp.jpg", "/eZ239CUp1d6OryZEBPnO2n87gMG.jpg", "2024-02-27", 8.1));
        list.add(createMovie(1022789, "Inside Out 2", "Teenager Riley's mind headquarters is undergoing a sudden demolition to make room for unexpected emotions!", "/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg", "/p5ozvmdgsmbWe0H8Xk7Rc8SCwAB.jpg", "2024-06-11", 7.5));
        list.add(createMovie(1241982, "Moana 2", "After receiving an unexpected call from her wayfinding ancestors, Moana journeys alongside Maui to the far seas of Oceania.", "/aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg", "/vYqt6kb4lcF8wwqsMMaULkP9OEn.jpg", "2024-11-21", 7.0));
        list.add(createMovie(872585, "Oppenheimer", "The story of J. Robert Oppenheimer's role in the development of the atomic bomb during World War II.", "/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg", "/neeNHeXjMF5fXoCJRsOmkNGC7q.jpg", "2023-07-19", 8.0));
        list.add(createMovie(76600, "Avatar: The Way of Water", "Set more than a decade after the events of the first film, learn the story of the Sully family...", "/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg", "/kJsPVzdyBrYHLomuNv5SJDXUQ2f.jpg", "2022-12-14", 7.6));
        list.add(createMovie(945961, "Alien: Romulus", "While scavenging the deep ends of a derelict space station, a group of young space colonizers come face to face with terrifying life forms.", "/2uSWRTtCG336nuBiG8jOTEUKSy8.jpg", "/iYqSQaWDttQIQzsxg9xHyg0bttG.jpg", "2024-08-13", 7.2));
        list.add(createMovie(762441, "A Quiet Place: Day One", "As New York City is invaded by alien creatures who hunt by sound, a woman named Sam fights to survive.", "/hU42CRk14JuPEdqZG3AWmagiPAP.jpg", "/6XjMwQTvnICBz6TguiDKkDVHvgS.jpg", "2024-06-26", 6.6));
        list.add(createMovie(786892, "Furiosa: A Mad Max Saga", "As the world falls, young Furiosa is snatched from the Green Place of Many Mothers into the hands of a great biker horde.", "/iADOJ8Zymht2JPMoy3R7xceZprc.jpg", "/raph7qjAGTMXaIjVxt6ZDSXRzUr.jpg", "2024-05-22", 7.4));
        list.add(createMovie(653346, "Kingdom of the Planet of the Apes", "Several generations following Caesar's reign, apes live harmoniously while humans have been reduced to living in the shadows.", "/gKkl37BQuKTanygYQG1pyYgLVgf.jpg", "/fypydCipcWDKDTTCoPucBsdGYXW.jpg", "2024-05-08", 7.0));
        list.add(createMovie(912649, "Venom: The Last Dance", "Eddie and Venom are on the run. Hunted by both of their worlds and with the net closing in, the duo are forced into a devastating decision.", "/vGXptEdgZIhPg3cGlc7e8sNPC2e.jpg", "/3V4kLQg0kSqPLctI5ziYWabAZYF.jpg", "2024-10-22", 6.7));
        list.add(createMovie(573435, "Bad Boys: Ride or Die", "After their late former Captain is framed, Lowrey and Burnett try to clear his name, only to end up on the run themselves.", "/oGythE98MYleE6mZlGs5oBGkux1.jpg", "/3q01ACG0MWm0DekhvkPFCXyPZSu.jpg", "2024-06-05", 7.3));
        list.add(createMovie(933260, "The Substance", "A fading celebrity decides to use a black market drug, a cell-replicating substance that temporarily creates a younger version of herself.", "/lqoMzCcZYEFK729d6qzt349fB4o.jpg", "/bVSOgrxasVJF6V71T7v2KfBRSzu.jpg", "2024-09-18", 7.1));
        list.add(createMovie(1100782, "Smile 2", "About to embark on a new world tour, global pop sensation Skye Riley begins experiencing increasingly terrifying and inexplicable events.", "/ht8Uv9QPv9y7K0RvUyJIaXOZTfd.jpg", "/iR79ciqhtaZ9BE7YFA1HpCHQgX4.jpg", "2024-10-16", 6.6));
        list.add(createMovie(917496, "Beetlejuice Beetlejuice", "After a family tragedy, three generations of the Deetz family return home to Winter River. Still haunted by Betelgeuse.", "/kKgQzkUCnQmeTPkyIwHly2t6ZFI.jpg", "/A1dZ6faTjg0e6HYftBmEKujuXGQ.jpg", "2024-09-04", 6.9));
        list.add(createMovie(603692, "John Wick: Chapter 4", "With the price on his head ever increasing, John Wick uncovers a path to defeating The High Table.", "/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg", "/7I6VUdPj6tQECNHdviJkUHD2u89.jpg", "2023-03-21", 7.7));
        list.add(createMovie(385687, "Fast X", "Over many missions and against impossible odds, Dom Toretto and his family have outsmarted every foe in their path.", "/fiVW06jE7z9YnO4trhaMEdclSiC.jpg", "/4XM8DUTQb3lhLemJC51Jx4a2EuA.jpg", "2023-05-17", 7.0));
        list.add(createMovie(575264, "Mission: Impossible - Dead Reckoning Part One", "Ethan Hunt and his IMF team embark on their most dangerous mission yet: To track down a terrifying new weapon.", "/NNxYkU70HPurnNCSiCjYAmacwm.jpg", "/628Dep6AxEtDxjZoGP78TsOxYbK.jpg", "2023-07-08", 7.5));
        list.add(createMovie(361743, "Top Gun: Maverick", "After more than thirty years of service as one of the Navy’s top aviators, Pete “Maverick” Mitchell finds himself training TOP GUN graduates.", "/n0YuM4f5lvGAP6MAW2kBIzugXnc.jpg", "/AaV1YIdWKnjAIAOe8UUKBFm327v.jpg", "2022-05-21", 8.2));
        list.add(createMovie(569094, "Spider-Man: Across the Spider-Verse", "Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its existence.", "/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg", "/kVd3a9YeLGkoeR50jGEXM6EqseS.jpg", "2023-05-31", 8.3));
        list.add(createMovie(157336, "Interstellar", "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass human space travel limitations.", "/yQvGrMoipbRoddT0ZR8tPoR7NfX.jpg", "/8sNiAPPYU14PUepFNeSNGUTiHW.jpg", "2014-11-04", 8.5));
        list.add(createMovie(1011985, "Kung Fu Panda 4", "Po is gearing up to become the spiritual leader of his Valley of Peace, but also needs someone to take his place as Dragon Warrior.", "/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg", "/3ffPx9jqg0yj9y1KWeagT7D20CB.jpg", "2024-03-02", 7.0));
        list.add(createMovie(519182, "Despicable Me 4", "Gru and Lucy and their girls welcome a new member to the Gru family, Gru Jr., who is intent on tormenting his dad.", "/wWba3TaojhK7NdycRhoQpsG0FaH.jpg", "/twsxsfao6ZOVvT8LfudH603MMi6.jpg", "2024-06-20", 7.0));

        return list;
    }

    public static List<TvShow> getDefaultTvShows() {
        List<TvShow> list = new ArrayList<>();

        // African & Kenyan Series
        list.add(createTvShow(200907, "Queen & Country", "Her 60 years on the throne make her the second longest-serving sovereign in history. A fascinating look into royal institutions.", "/yl8uWtaKGDUIla7JiTGExxzydji.jpg", "/4dUOy6SVpjsA6WB21xFOqjrkKbj.jpg", "2012-07-01", 7.0));
        list.add(createTvShow(229205, "Crime and Justice", "Leading Kenyan investigative procedural following top detectives solving high-profile crimes in Nairobi.", "/qLvoPmfyi1vEAo6KCdICTiYvnGS.jpg", "/qLvoPmfyi1vEAo6KCdICTiYvnGS.jpg", "2021-02-25", 7.8));
        list.add(createTvShow(103411, "Blood & Water", "After crossing paths at a party, a Cape Town teen sets out to prove whether a private-school swimming star is her sister who was abducted at birth.", "/NFHy3z1taQ8nzudDHeVIJnih3J.jpg", "/hNNUmhE5dv5tzp4ONHTl3JULAOf.jpg", "2020-05-20", 7.8));
        list.add(createTvShow(77742, "The Blood Sisters", "Triplets, separated at birth, reunite under the most chaotic circumstances that will test their bond as sisters.", "/c6nDyiaWda9az3LlHx6B3fYl0xm.jpg", "/irgsSnL46hymuXTu63zr7gWd8uG.jpg", "2018-02-12", 8.3));
        list.add(createTvShow(215874, "Far from Home", "Through a scholarship to a top school, Ishaya ends up in the luxurious world of the happy few in Nigeria, but a secret threatens everything.", "/svGLFlbjDKx67CVUEFtPGooW25F.jpg", "/r178SNOqH4VD1aydevTdiULhJ4R.jpg", "2022-12-16", 8.1));

        // Global TV Series Blockbusters
        list.add(createTvShow(126308, "Shōgun", "In Japan in the year 1600, Lord Yoshii Toranaga is fighting for his life as his enemies on the Council of Regents unite against him.", "/7O4iVfOMQmdCSxhOg1WnzG1AgYT.jpg", "/bwSmgmd90hCWwqOKQYTEraeOZhJ.jpg", "2024-02-27", 8.4));
        list.add(createTvShow(94997, "House of the Dragon", "The Targaryen dynasty is at the absolute apex of its power, with more than 15 dragons under their yoke.", "/7V0Ebks0GgpKvQ7QbLAIdX5dos4.jpg", "/577eXC8wFQT0eUrJcgznSiFPRmk.jpg", "2022-08-21", 8.4));
        list.add(createTvShow(93405, "Squid Game", "Hundreds of cash-strapped players accept a strange invitation to compete in children's games. Inside, a tempting prize awaits with deadly stakes.", "/1QdXdRYfktUSONkl1oD5gc6Be0s.jpg", "/2meX1nMdScFOoV4370rqHWKmXhY.jpg", "2021-09-17", 7.9));
        list.add(createTvShow(66732, "Stranger Things", "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces, and Eleven.", "/uOOtwVbSr4QDjAGIifLDwpb2Pdl.jpg", "/9P4IIMYY3HifqeruZq0ZZ9g7YUi.jpg", "2016-07-15", 8.5));
        list.add(createTvShow(106379, "Fallout", "200 years after the apocalypse, the gentle denizens of luxury fallout shelters are forced to return to the irradiated hellscape.", "/c15BtJxCXMrISLVmysdsnZUPQft.jpg", "/coaPCIqQBPUZsOnJcWZxhaORcDT.jpg", "2024-04-10", 8.1));
        list.add(createTvShow(76479, "The Boys", "A group of vigilantes known as 'The Boys' set out to take down corrupt superheroes with blue-collar grit.", "/in1R2dDc421JxsoRWaIIAqVI2KE.jpg", "/bq28ajZaoMyzEIm6REelqyqtEDZ.jpg", "2019-07-25", 8.4));
        list.add(createTvShow(194764, "The Penguin", "With the city in peril following the seawall's collapse, Oswald 'Oz' Cobb seeks to fill the power vacuum left by Carmine Falcone.", "/vOWcqC4oDQws1doDWLO7d3dh5qc.jpg", "/4TdmuuwiIiKw3JOjIuhdgYxRXnN.jpg", "2024-09-19", 8.3));
        list.add(createTvShow(108978, "Reacher", "Jack Reacher, a veteran military police investigator, enters civilian life and explores the nation he once served.", "/f1VCQIG2iCyOookdgOzwtUpwWC0.jpg", "/pF0qkRsrHkdYadPWY9AMeFZfcwk.jpg", "2022-02-03", 8.1));
        list.add(createTvShow(84958, "Loki", "After stealing the Tesseract during Avengers Endgame, an alternate Loki is brought to the Time Variance Authority.", "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg", "/q3jHCb4dMfYF6ojikKuHd6LscxC.jpg", "2021-06-09", 8.2));
        list.add(createTvShow(119051, "Wednesday", "Smart, sarcastic and a little dead inside, Wednesday Addams investigates twisted mysteries while making new friends and foes.", "/9PFonBhy4cQy7Jz20NpMygczOkv.jpg", "/iHSwvRVsRyxpX7FE7GbviaDvgGZ.jpg", "2022-11-23", 8.3));
        list.add(createTvShow(100088, "The Last of Us", "Twenty years after modern civilization has been destroyed, Joel is hired to smuggle 14-year-old Ellie across the United States.", "/dmo6TYuuJgaYinXBPjrgG9mB5od.jpg", "/lY2DhbA7Hy44fAKddr06UrXWWaQ.jpg", "2023-01-15", 8.4));
        list.add(createTvShow(1396, "Breaking Bad", "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing methamphetamine to secure his family's future.", "/anFx9aTOOYqgS3v7x3R84Kz67ly.jpg", "/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg", "2008-01-20", 9.0));
        list.add(createTvShow(1399, "Game of Thrones", "Seven noble families fight for control of the mythical land of Westeros, while an ancient evil awakens in the farthest north.", "/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg", "/zZqpAXxVSBtxV9qPBcscfXBcL2w.jpg", "2011-04-17", 8.5));

        // Anime Hits
        list.add(createTvShow(85937, "Demon Slayer: Kimetsu no Yaiba", "After a demon attack leaves his family slain and his sister cursed, Tanjiro embarks upon a perilous journey to find a cure.", "/xUfRZu2mi8jH6SzQEJGP6tjBuYj.jpg", "/3GQKYh6Trm8pxd2AypovoYQf4Ay.jpg", "2019-04-06", 8.6));
        list.add(createTvShow(1429, "Attack on Titan", "Humanity is forced to retreat behind giant walls to escape massive, man-eating Titans. Eren Jaeger pledges to eradicate them all.", "/hTP1DtLGFamjfu8WqjnuQdP1n4i.jpg", "/rqbCbjB19amtOtFQbb3K2lgm2zv.jpg", "2013-04-07", 8.7));
        list.add(createTvShow(127532, "Solo Leveling", "In a world where hunters fight deadly monsters, Sung Jinwoo is the weakest hunter who gains the ability to level up infinitely.", "/geCRueV3ElhRTr0xtJuEWJt6dJ1.jpg", "/xMNH87maNLt9n2bMDYeI6db5VFm.jpg", "2024-01-07", 8.7));

        return list;
    }

    private static Movie createMovie(int id, String title, String overview, String poster, String backdrop, String date, double rating) {
        Movie m = new Movie();
        try {
            java.lang.reflect.Field fId = Movie.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(m, id);

            java.lang.reflect.Field fTitle = Movie.class.getDeclaredField("title");
            fTitle.setAccessible(true);
            fTitle.set(m, title);

            java.lang.reflect.Field fOverview = Movie.class.getDeclaredField("overview");
            fOverview.setAccessible(true);
            fOverview.set(m, overview);

            java.lang.reflect.Field fPoster = Movie.class.getDeclaredField("posterPath");
            fPoster.setAccessible(true);
            fPoster.set(m, poster);

            java.lang.reflect.Field fBackdrop = Movie.class.getDeclaredField("backdropPath");
            fBackdrop.setAccessible(true);
            fBackdrop.set(m, backdrop);

            java.lang.reflect.Field fDate = Movie.class.getDeclaredField("releaseDate");
            fDate.setAccessible(true);
            fDate.set(m, date);

            java.lang.reflect.Field fRating = Movie.class.getDeclaredField("voteAverage");
            fRating.setAccessible(true);
            fRating.set(m, rating);
        } catch (Exception ignored) {}
        return m;
    }

    private static TvShow createTvShow(int id, String name, String overview, String poster, String backdrop, String date, double rating) {
        TvShow t = new TvShow();
        try {
            java.lang.reflect.Field fId = TvShow.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(t, id);

            java.lang.reflect.Field fName = TvShow.class.getDeclaredField("name");
            fName.setAccessible(true);
            fName.set(t, name);

            java.lang.reflect.Field fOverview = TvShow.class.getDeclaredField("overview");
            fOverview.setAccessible(true);
            fOverview.set(t, overview);

            java.lang.reflect.Field fPoster = TvShow.class.getDeclaredField("posterPath");
            fPoster.setAccessible(true);
            fPoster.set(t, poster);

            java.lang.reflect.Field fBackdrop = TvShow.class.getDeclaredField("backdropPath");
            fBackdrop.setAccessible(true);
            fBackdrop.set(t, backdrop);

            java.lang.reflect.Field fDate = TvShow.class.getDeclaredField("firstAirDate");
            fDate.setAccessible(true);
            fDate.set(t, date);

            java.lang.reflect.Field fRating = TvShow.class.getDeclaredField("voteAverage");
            fRating.setAccessible(true);
            fRating.set(t, rating);
        } catch (Exception ignored) {}
        return t;
    }
}
