import React, { useState } from "react";
import { Card } from "../ui/Card";
import { Input } from "../ui/Input";
import { Button } from "../ui/Button";

type Props = { onSearch?: (q: string) => void };

export const SearchBar: React.FC<Props> = ({ onSearch }) => {
  const [q, setQ] = useState("");

  return (
    <Card className="mx-auto w-full max-w-4xl p-6">
      <div className="mb-4">
        <Input
          placeholder="Search properties..."
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
      </div>
      <div className="mt-6 flex justify-center">
        <Button onClick={() => onSearch?.(q)}>Start Search</Button>
      </div>
    </Card>
  );
};
