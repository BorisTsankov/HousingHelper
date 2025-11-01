export type Filters = {
  type?: "Apartment" | "House" | "Studio" | "";
  price?: "" | "$500 - $1,000" | "$1,000 - $1,500" | "$2,500+";
  location?: "" | "Eindhoven" | "Rotterdam" | "Amsterdam";
};