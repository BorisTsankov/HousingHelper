type SelectProps = React.SelectHTMLAttributes<HTMLSelectElement>;
export const Select = (props: SelectProps) => (
  <select {...props}
    className={`w-full appearance-none rounded-xl border border-slate-300 bg-white px-4 py-3 text-slate-700 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 ${props.className ?? ""}`} />
);