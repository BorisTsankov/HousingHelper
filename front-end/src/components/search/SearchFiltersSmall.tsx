import { Select } from "../ui/Select";
import type { Filters } from "../../types/filters";

type Props = {
    value: Filters;
    onChange: (f: Filters) => void };

export const SearchFilters: React.FC<Props> = ({ value, onChange }) => {
  const update = (k: keyof Filters) => (e: React.ChangeEvent<HTMLSelectElement>) =>
    onChange({ ...value, [k]: e.target.value as any });

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
      <Select value={value.type ?? ""} onChange={update("type")}>
        <option value="">Property Type</option>
        <option>Apartment</option>
        <option>House</option>
        <option>Studio</option>
      </Select>

      <Select value={value.price ?? ""} onChange={update("price")}>
        <option value="">Price Range</option>
        <option>$500 - $1,000</option>
        <option>$1,000 - $1,500</option>
        <option>$2,500+</option>
      </Select>

      <Select value={value.location ?? ""} onChange={update("location")}>
        <option value="">Location</option>
        <option>Eindhoven</option>
        <option>Rotterdam</option>
        <option>Amsterdam</option>
      </Select>
    </div>
  );
};