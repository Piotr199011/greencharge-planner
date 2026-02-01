// src/components/charts/EnergyPieChart.tsx
import React from "react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { formatNumber } from "../../utils/formatNumber";

interface PieData {
  name: string;
  value: number;
}

interface Props {
  data: PieData[];
}

const COLORS = [
  "#4caf50", "#2196f3", "#ff9800", "#9c27b0",
  "#f44336", "#00bcd4", "#cddc39", "black"
];

const EnergyPieChart: React.FC<Props> = ({ data }) => {
  if (!data || data.length === 0) return <p>No charts to display</p>;

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={data}
          dataKey="value"
          nameKey="name"
          cx="50%"
          cy="50%"
          outerRadius={100}
          label={(entry) => `${formatNumber(entry.value)}%`}
          labelLine={false}
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip formatter={(value: number) => `${formatNumber(value)}%`} />
        <Legend
          verticalAlign="bottom"
          height={36}
          iconType="circle"
          formatter={(value, entry, index) => (
            <span style={{ fontSize: 12 }}>{value}</span>
          )}
        />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default EnergyPieChart;
