package com.matchme.match_me.seed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.matchme.match_me.auth.PasswordService;
import com.matchme.match_me.bio.Bio;
import com.matchme.match_me.bio.BioRepository;
import com.matchme.match_me.location.Location;
import com.matchme.match_me.location.LocationRepository;
import com.matchme.match_me.profile.Profile;
import com.matchme.match_me.profile.ProfileRepository;
import com.matchme.match_me.users.User;
import com.matchme.match_me.users.UserRepository;

@Configuration
@org.springframework.context.annotation.Profile("seed") //Only runs when --spring.profiles.active=seed
public class DataSeeder {

    private static final Random RANDOM = new Random();

    private static final String[] FIRST_NAMES = {
        "Katrin", "Toomas", "Mari", "Jüri", "Liis", "Peeter", "Kadi", "Margus", "Kristina", "Andres",
        "Annika", "Mati", "Helen", "Tarmo", "Piret", "Priit", "Triin", "Raivo", "Kerli", "Jaanus",
        "Maris", "Kalle", "Eve", "Meelis", "Sirje", "Mart", "Anne", "Urmas", "Tiiu", "Ants",
        "Riina", "Rein", "Merle", "Heiki", "Inge", "Tõnis", "Laura", "Indrek", "Maire", "Raul",
        "Kadri", "Martin", "Silva", "Jaan", "Anneli", "Toivo", "Krista", "Ivo", "Meeli", "Ain"
    };

    private static final String[] HOBBIES = {
        "hiking, photography, reading", "gaming, coding, music production", "cooking, yoga, gardening",
        "traveling, blogging, painting", "running, cycling, swimming", "dancing, singing, theater",
        "rock climbing, camping, kayaking", "chess, puzzles, board games", "knitting, crafting, DIY projects",
        "basketball, soccer, tennis", "meditation, mindfulness, journaling", "woodworking, metalworking, pottery"
    };

    private static final String[] INTERESTS = {
        "technology, science, innovation", "art, culture, history", "fitness, health, wellness",
        "business, entrepreneurship, finance", "environment, sustainability, nature", "psychology, philosophy, sociology",
        "film, literature, poetry", "fashion, design, architecture", "politics, social justice, activism",
        "astronomy, space exploration, physics", "food, culinary arts, wine tasting", "animals, veterinary medicine, conservation"
    };

    private static final String[] FOOD_PREFERENCES = {
        "vegetarian, italian, sushi", "vegan, thai, mediterranean", "pescatarian, japanese, korean",
        "omnivore, mexican, indian", "italian, french, american", "chinese, vietnamese, indonesian",
        "greek, turkish, lebanese", "bbq, steaks, burgers", "organic, farm-to-table, local",
        "spicy food, street food, ethnic cuisine", "desserts, pastries, baked goods", "seafood, sushi, oysters"
    };

    private static final String[] MUSIC_TASTES = {
        "indie, alternative, rock", "pop, r&b, hip-hop", "electronic, edm, house",
        "jazz, blues, soul", "classical, opera, orchestral", "country, folk, bluegrass",
        "metal, punk, hardcore", "reggae, ska, world music", "k-pop, j-pop, anime soundtracks",
        "latin, salsa, bachata", "rap, trap, grime", "ambient, lo-fi, chillwave"
    };

    private static final String[] PERSONALITY_TYPES = {
        "INTJ - strategic, analytical", "ENFP - enthusiastic, creative", "ISTJ - responsible, organized",
        "ESFP - spontaneous, energetic", "INFJ - insightful, idealistic", "ENTP - innovative, curious",
        "ISFJ - nurturing, detail-oriented", "ESTJ - efficient, practical", "INFP - empathetic, artistic",
        "ESTP - bold, action-oriented", "INTP - logical, thoughtful", "ESFJ - caring, sociable"
    };

    private static final String[] LOOKING_FOR = {
        "friendship, networking", "dating, romance", "collaboration, projects",
        "mentorship, learning", "adventure partners, travel buddies", "study groups, academic support",
        "workout partners, fitness motivation", "creative partnerships, art projects", "business connections, entrepreneurship",
        "casual hangouts, coffee dates", "long-term relationship, commitment", "gaming friends, online community"
    };

    private static final String[][] CITIES_WITH_COORDS = {
        {"Tallinn", "59.4370", "24.7536"},
        {"Tartu", "58.3780", "26.7290"},
        {"Narva", "59.3772", "28.1903"},
        {"Pärnu", "58.3859", "24.4971"},
        {"Kohtla-Järve", "59.3986", "27.2731"},
        {"Viljandi", "58.3639", "25.5900"},
        {"Rakvere", "59.3469", "26.3561"},
        {"Maardu", "59.4650", "25.0133"},
        {"Sillamäe", "59.3970", "27.7628"},
        {"Kuressaare", "58.2489", "22.4894"},
        {"Võru", "57.8394", "27.0142"},
        {"Valga", "57.7781", "26.0461"},
        {"Haapsalu", "58.9433", "23.5413"},
        {"Jõhvi", "59.3586", "27.4150"},
        {"Paide", "58.8856", "25.5569"},
        {"Keila", "59.3050", "24.4169"},
        {"Kiviõli", "59.3533", "26.9697"},
        {"Tapa", "59.2608", "25.9589"},
        {"Põlva", "58.0564", "27.0586"},
        {"Türi", "58.8086", "25.4272"}
    };

    @Bean
    @org.springframework.context.annotation.Profile("seed")
    public CommandLineRunner seedData(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            BioRepository bioRepository,
            LocationRepository locationRepository,
            PasswordService passwordService) {

        return args -> {
            System.out.println("🌱 Starting data seeding...");

            //Check if data already exists
            if (userRepository.count() > 10) {
                System.out.println("⚠️ Database already contains data. Skipping seed.");
                return;
            }

            List<User> users = new ArrayList<>();

            for (int i = 0; i < 120; i++) {
                String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
                // ensure uniqueness to avoid constraint violations
                String username = firstName.toLowerCase() + i;
                String email = username + "@matchme.example.com";

                //Create user
                User user = new User();
                user.setEmail(email);
                user.setUsername(username);
                user.setPasswordHash(passwordService.hash("password123"));
                user = userRepository.save(user);

                //Create profile
                Profile profile = new Profile();
                profile.setUser(user);
                profile.setDisplayName(firstName + " " + (char)('A' + RANDOM.nextInt(26)) + ".");
                profile.setAboutMe(generateAboutMe());
                profile.setAvatarUrl(null); // Placeholder
                profileRepository.save(profile);

                //Create bio
                Bio bio = new Bio();
                bio.setUser(user);
                bio.setHobbies(HOBBIES[RANDOM.nextInt(HOBBIES.length)]);
                bio.setInterests(INTERESTS[RANDOM.nextInt(INTERESTS.length)]);
                bio.setFoodPreferences(FOOD_PREFERENCES[RANDOM.nextInt(FOOD_PREFERENCES.length)]);
                bio.setMusicTaste(MUSIC_TASTES[RANDOM.nextInt(MUSIC_TASTES.length)]);
                bio.setPersonalityType(PERSONALITY_TYPES[RANDOM.nextInt(PERSONALITY_TYPES.length)]);
                bio.setLookingFor(LOOKING_FOR[RANDOM.nextInt(LOOKING_FOR.length)]);
                bioRepository.save(bio);

                //Create location
                String[] cityData = CITIES_WITH_COORDS[RANDOM.nextInt(CITIES_WITH_COORDS.length)];
                Location location = new Location();
                location.setUser(user);
                location.setCity(cityData[0]);
                location.setLatitude(Double.parseDouble(cityData[1]));
                location.setLongitude(Double.parseDouble(cityData[2]));
                location.setMaxRadiusKm(10.0 + RANDOM.nextDouble() * 90.0); // 10-100 km
                locationRepository.save(location);

                users.add(user);

                if ((i + 1) % 20 == 0) {
                    System.out.println("✅ Created " + (i + 1) + " users...");
                }
            }

            System.out.println("🎉 Seeding complete! Created " + users.size() + " users with full profiles.");
            System.out.println("📧 All users have password: password123");
        };
    }

    private String generateAboutMe() {
        String[] templates = {
            "Passionate about life and always looking for the next adventure. Love meeting new people and exploring new places.",
            "Creative soul with a love for art and music. Enjoy deep conversations and spontaneous road trips.",
            "Tech enthusiast by day, foodie by night. Always up for trying new restaurants and experiencing different cultures.",
            "Fitness junkie who believes in living life to the fullest. Looking for someone to share adventures with.",
            "Bookworm and coffee addict. Enjoy quiet evenings at home but also love exploring the city.",
            "Outdoor enthusiast who loves hiking, camping, and anything involving nature. Let's go on an adventure!",
            "Aspiring entrepreneur with big dreams. Looking to connect with like-minded individuals.",
            "Music lover and concert goer. Nothing beats live music and good company.",
            "World traveler with stories to share. Always planning the next trip and seeking travel buddies.",
            "Dog lover, yoga practitioner, and meditation enthusiast. Seeking positive vibes and genuine connections."
        };
        return templates[RANDOM.nextInt(templates.length)];
    }
}
