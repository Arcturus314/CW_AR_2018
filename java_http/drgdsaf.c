char x_0;
char x_1;

short full_val = ((x_1 & 0x07) << 8 ) | x_1;

float converted_val;

converted_val = ((float) (full_val / 8191)) * 16.0;

13 bit field is 2^13-1
