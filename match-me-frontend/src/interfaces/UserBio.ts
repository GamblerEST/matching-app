export interface UserBio {
    id: number;
    userId: number;
    hobbies: string;
    interests: string;
    foodPreferences: string;
    musicTaste: string;
    personalityType: string;
    lookingFor: string;
}

export type BioTypeaheadField =
        | "hobbies"
        | "interests"
        | "foodPreferences"
        | "musicTaste"
        | "personalityType"
        | "lookingFor";