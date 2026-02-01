export const formatNumber = (value: number, decimals = 2): number =>
  Number(value.toFixed(decimals))

export const formatMixForChart = (mix?: Record<string, number> | { name: string; value: number }[]) => {
  if (!mix) return []

  const mixArray = Array.isArray(mix)
    ? mix
    : Object.entries(mix).map(([name, value]) => ({ name, value }))

  return mixArray.map(item => ({
    name: item.name,
    value: formatNumber(item.value)
  }))
}
